package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.Board;
import PickMeal.PickMeal.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;

    public List<Board> showBoard() {
        return boardMapper.showBoard();
    }

    public void writeBoard(Board board) {
        boardMapper.writeBoard(board);
    }

    public void removeBoard() {
        boardMapper.removeBoard();
    }
}
