package PickMeal.PickMeal.service;

import PickMeal.PickMeal.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;

    public void showBoard() {
        boardMapper.showBoard();
    }

    public void writeBoard() {
        boardMapper.writeBoard();
    }

    public void removeBoard() {
        boardMapper.removeBoard();
    }
}
