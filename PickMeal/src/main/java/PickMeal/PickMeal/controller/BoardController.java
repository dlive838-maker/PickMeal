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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/list")
    public String showBoard(@PageableDefault(size = 10, sort = "boardId", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<Board> boards = boardService.findBoardAll(pageable);
        List<String> user_nicknames = boards.stream().map(board -> userService.findByUser_id(board.getUser_id())).toList();
        model.addAttribute("boards", boards);
        model.addAttribute("user_nicknames", user_nicknames);
        return "/board/board";
    }

    @GetMapping("/write")
    public String writeBoardForm() {
        return "/board/board-write";
    }

    @PostMapping("/write")
    public String writeBoard(Board board, @RequestParam(value = "file", required = false) MultipartFile file, @AuthenticationPrincipal User user) {
        board.setUser_id(user.getUser_id());
        boardService.writeBoard(board);

        if(file != null && !file.isEmpty()){
            fileService.saveFile(board.getBoardId(), file);
        }
        return "redirect:/board/list";
    }

    @GetMapping("/detail/{boardId}")
    public String showBoardDetail(@PathVariable long boardId, @AuthenticationPrincipal User user, Model model) {
        if(boardService.getBoardIdByUser_id(user.getUser_id()).contains(boardId)){
            model.addAttribute("isWriter", true);
        }
        else {
            model.addAttribute("isWriter", false);
        }
        boardService.updateViewCount(boardId);
        Board board = boardService.getBoardByBoardId(boardId);
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
