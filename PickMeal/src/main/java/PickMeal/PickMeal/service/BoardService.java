package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.Board;
import PickMeal.PickMeal.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;

    public Page<Board> findBoardAll(Pageable pageable) {
        List<Board> boards = boardMapper.findBoardAll(pageable);
        int total = boardMapper.countBoard();
        return new PageImpl<>(boards, pageable, total);
    }

    public void writeBoard(Board board) {
        boardMapper.writeBoard(board);
    }

    public void removeBoard() {
        boardMapper.removeBoard();
    }

    public List<Long> getBoardIdByUser_id(long userId) {return boardMapper.getBoardIdByUser_id(userId);
    }

    public Board getBoardByBoardId(long boardId) {return boardMapper.getBoardByBoardId(boardId);
    }
}
