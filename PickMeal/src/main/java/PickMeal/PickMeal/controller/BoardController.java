package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.Board;
import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.service.BoardService;
import PickMeal.PickMeal.service.FileService;
import PickMeal.PickMeal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final FileService fileService;
    private final UserService userService;

    // [수정] 공통 도우미 메서드: 유저 식별값(String)을 반환합니다.
    private String getLoginUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            // 일반 유저는 long 타입 id를 문자열로 변환하여 반환
            return String.valueOf(((User) principal).getUser_id());
        } else if (principal instanceof OAuth2User) {
            // 소셜 유저는 OAuth2User의 name(식별값)을 반환
            return ((OAuth2User) principal).getName();
        }
        return null;
    }

    @GetMapping("/list")
    public String showBoard(@PageableDefault(size = 10, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<Board> boards = boardService.findBoardAll(pageable);
        // [수정] user_id가 long이므로 String으로 변환하여 닉네임을 조회하도록 처리
        List<String> user_nicknames = boards.stream()
                .map(board -> userService.findByUser_id(board.getUser_id()))
                .toList();
        model.addAttribute("boards", boards);
        model.addAttribute("user_nicknames", user_nicknames);
        return "/board/board";
    }

    @GetMapping("/write")
    public String writeBoardForm() {
        return "/board/board-write";
    }

    @PostMapping("/write")
    public String writeBoard(Board board, @RequestParam(value = "file", required = false) MultipartFile file, Authentication authentication) {
        // 1. 현재 어떤 소셜 서비스인지 확인 (예: kakao, naver 등)
        String registrationId = "";
        if (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) {
            registrationId = ((org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        }

        // 2. 식별자 가져오기
        String principalName = getLoginUserId(authentication);

        // 3. [핵심] DB 저장 형식에 맞춰서 ID를 재조합 (예: kakao_4746951582)
        String fullUserId = registrationId.isEmpty() ? principalName : registrationId + "_" + principalName;

        System.out.println("DB 조회를 위한 풀 아이디: " + fullUserId);

        // 4. 재조합된 ID로 유저 찾기
        User loginUser = userService.findById(fullUserId);

        if (loginUser != null) {
            board.setUser_id(loginUser.getUser_id()); // 진짜 숫자(long) PK를 넣어줌
            boardService.writeBoard(board);
        } else {
            System.out.println("여전히 유저를 못 찾음: " + fullUserId);
            return "redirect:/users/login";
        }

        if(file != null && !file.isEmpty()){
            fileService.saveFile(board.getBoardId(), file);
        }
        return "redirect:/board/list";
    }

    @GetMapping("/detail/{boardId}")
    public String showBoardDetail(@PathVariable long boardId, Authentication authentication, Model model) {
        // 1. 게시글 정보를 가져옵니다.
        Board board = boardService.getBoardByBoardId(boardId);

        // 2. 로그인 상태라면 작성자 비교를 수행합니다.
        if (authentication != null && authentication.isAuthenticated()) {
            // 이전에 글쓰기에서 썼던 로직을 그대로 가져와서 진짜 user_id를 찾습니다.
            String principalName = getLoginUserId(authentication);

            // 소셜 로그인 구분 (카카오/네이버 등)
            String registrationId = "";
            if (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) {
                registrationId = ((org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
            }
            String fullUserId = registrationId.isEmpty() ? principalName : registrationId + "_" + principalName;

            // DB에서 현재 로그인한 사람의 진짜 객체를 가져옵니다.
            User loginUser = userService.findById(fullUserId);

            // [핵심 비교] 게시글의 user_id와 로그인한 사람의 user_id가 같으면 작성자입니다.
            if (loginUser != null && board.getUser_id() == loginUser.getUser_id()) {
                model.addAttribute("isWriter", true);
            } else {
                model.addAttribute("isWriter", false);
            }
        } else {
            model.addAttribute("isWriter", false);
        }

        boardService.updateViewCount(boardId);
        model.addAttribute("board", board);
        model.addAttribute("files", fileService.findByBoardId(boardId));
        return "/board/board-detail";
    }

    @GetMapping("/edit/{boardId}")
    public String editBoardForm(@PathVariable long boardId, Model model) {
        Board board = boardService.getBoardByBoardId(boardId);
        model.addAttribute("board", board);
        model.addAttribute("files", fileService.findByBoardId(boardId));
        return "/board/board-edit";
    }

    @PostMapping("/edit/{boardId}")
    public String editBoard(Board board, @PathVariable long boardId, Model model, @RequestParam(value = "file", required = false) MultipartFile file) {
        board.setBoardId(boardId);
        boardService.editBoard(board);

        if(file != null && !file.isEmpty()){
            fileService.deleteByBoardId(boardId);
            fileService.saveFile(boardId, file);
        }
        return "redirect:/board/detail/" + boardId;
    }

    @PostMapping("/remove/{boardId}")
    public String removeBoard(@PathVariable long boardId) {
        boardService.removeBoard(boardId);
        fileService.deleteByBoardId(boardId);
        return "redirect:/board/list";
    }
}