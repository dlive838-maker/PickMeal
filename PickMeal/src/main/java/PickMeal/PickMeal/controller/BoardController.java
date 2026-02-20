package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.Board;
import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.service.BoardService;
import PickMeal.PickMeal.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;
    private final FileService fileService;

    @RequestMapping("/list")
    public String showBoard(Model model) {
        List<Board> boards = boardService.showBoard();
        model.addAttribute("boards", boards);
        return "/boards/board";
    }

    @RequestMapping("/write")
    public String writeBoardForm() {
        return "/boards/board-write";
    }

    @PostMapping("/write")
    public String writeBoard(Board board, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal User user) {
        board.setUser_id(user.getUser_id());
        boardService.writeBoard(board);

        if(file != null && !file.isEmpty()){
            fileService.saveFile(board.getBoardId(), file);
        }
        return "redirect:/boards/board";
    }

    @PostMapping("/remove")
    public String removeBoard() {
        boardService.removeBoard();
        return "redirect:/boards/list";
    }

}
