package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.Board;
import PickMeal.PickMeal.domain.Comment;
import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.domain.oauth.OAuth2Attributes;
import PickMeal.PickMeal.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final BoardReactionService boardReactionService;
    private final CommentService commentService;

    // [수정] 공통 도우미 메서드: 유저 식별값(String)을 반환합니다.
    private String getLoginUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;

        // 복잡한 타입 체크 대신 시큐리티가 제공하는 기본 Name(ID)을 사용합니다.
        return authentication.getName();
    }

    @GetMapping("/list")
    public String showBoard(@PageableDefault(size = 10, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<Board> boards = boardService.findBoardAll(pageable);

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
        // [수정] 서비스의 공통 메서드를 사용하여 유저 객체를 바로 가져옵니다.
        User loginUser = userService.getAuthenticatedUser(authentication);

        if (loginUser != null) {
            board.setUser_id(loginUser.getUser_id());
            boardService.writeBoard(board);
        } else {
            // 유저를 찾지 못하면 로그인 페이지로 보냅니다.
            return "redirect:/users/login";
        }

        if(file != null && !file.isEmpty()){
            fileService.saveFile(board.getBoardId(), file);
        }
        return "redirect:/board/list";
    }

    @GetMapping("/detail/{boardId}")
    public String showBoardDetail(@PathVariable Long boardId, Authentication authentication, Model model) {
        Board board = boardService.getBoardByBoardId(boardId);
        User loginUser = userService.getAuthenticatedUser(authentication);

        boolean isWriter = false;
        int userReaction = 0;

        if (loginUser != null) {
            // 1. 좋아요 상태 확인
            userReaction = boardReactionService.isLikeOrDislike(boardId, loginUser.getUser_id());

            // 2. 작성자 비교 (Objects.equals 사용으로 널 체크와 타입 비교를 동시에)
            isWriter = java.util.Objects.equals(board.getUser_id(), loginUser.getUser_id());

            // 3. 디버깅용 로그 (서버 콘솔에서 확인 필수!)
            System.out.println("=== 작성자 확인 로그 ===");
            System.out.println("게시글 작성자 PK: " + board.getUser_id());
            System.out.println("현재 로그인 유저 PK: " + loginUser.getUser_id());
            System.out.println("결과 (isWriter): " + isWriter);

            model.addAttribute("user", loginUser);
        }

        model.addAttribute("board", board);
        model.addAttribute("isWriter", isWriter); // 블록 밖에서 명시적으로 전달
        model.addAttribute("userReaction", userReaction);
        model.addAttribute("comments", commentService.getCommentsByBoardId(boardId));
        model.addAttribute("files", fileService.findByBoardId(boardId));

        return "/board/board-detail";
    }

    @PostMapping("/reaction/{boardId}")
    @ResponseBody
    public ResponseEntity<String> boardLikeOrDislikeReaction(@PathVariable Long boardId, Authentication authentication, @RequestParam("like_type") int like_type) {
        // [수정] 중복 제거된 공통 메서드 활용
        User user = userService.getAuthenticatedUser(authentication);

        if (user == null) return ResponseEntity.status(401).body("LOGIN_REQUIRED");

        int currentStatus = boardReactionService.isLikeOrDislike(boardId, user.getUser_id());

        if(like_type > 0){
            if(currentStatus < 0) {
                boardService.removeDislikeCount(boardId);
                boardService.addLikeCount(boardId);
                boardReactionService.removeLikeOrDislikeReaction(boardId, user.getUser_id());
                boardReactionService.boardLikeOrDislikeReaction(boardId, user.getUser_id(), 1);

            }else if(currentStatus > 0){
                boardService.removeLikeCount(boardId);
                boardReactionService.removeLikeOrDislikeReaction(boardId, user.getUser_id());

            }else{
                boardService.addLikeCount(boardId);
                boardReactionService.boardLikeOrDislikeReaction(boardId, user.getUser_id(), 1);
            }
        }
        else if(like_type < 0){
            if(currentStatus > 0){
                boardService.removeLikeCount(boardId);
                boardReactionService.removeLikeOrDislikeReaction(boardId, user.getUser_id());
                boardService.addDislikeCount(boardId);
                boardReactionService.boardLikeOrDislikeReaction(boardId, user.getUser_id(), -1);

            }else if(currentStatus < 0) {
                boardService.removeDislikeCount(boardId);
                boardReactionService.removeLikeOrDislikeReaction(boardId, user.getUser_id());
            }else{
                boardService.addDislikeCount(boardId);
                boardReactionService.boardLikeOrDislikeReaction(boardId, user.getUser_id(), -1);
            }
        }
        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping("/edit/{boardId}")
    public String editBoardForm(@PathVariable Long boardId, Model model) {
        Board board = boardService.getBoardByBoardId(boardId);
        model.addAttribute("board", board);
        model.addAttribute("files", fileService.findByBoardId(boardId));
        return "/board/board-edit";
    }

    @PostMapping("/edit/{boardId}")
    public String editBoard(Board board, @PathVariable Long boardId, @RequestParam(value = "file", required = false) MultipartFile file) {
        board.setBoardId(boardId);
        boardService.editBoard(board);

        if(file != null && !file.isEmpty()){
            fileService.deleteByBoardId(boardId);
            fileService.saveFile(boardId, file);
        }
        return "redirect:/board/detail/" + boardId;
    }

    @PostMapping("/remove/{boardId}")
    public String removeBoard(@PathVariable Long boardId) {
        boardService.removeBoard(boardId);
        fileService.deleteByBoardId(boardId);
        return "redirect:/board/list";
    }
}