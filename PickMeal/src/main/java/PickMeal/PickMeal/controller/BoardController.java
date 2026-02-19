package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;

    @RequestMapping("/list")
    public String showBoard() {
        boardService.showBoard();
        return "/boards/board";
    }

    @RequestMapping("/write")
    public String writeBoardForm() {
        return "/boards/board-write";
    }

    @RequestMapping("/write")
    public String writeBoard() {
        return "redirect:/boards/list";
    }

    @PostMapping("/remove")
    public String removeBoard() {
        boardService.removeBoard();
        return "redirect:/boards/list";
    }

}
