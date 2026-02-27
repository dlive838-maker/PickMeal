package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.Board;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<Board> findBoardAll(Pageable pageable);

    int countBoard();

    void writeBoard(Board board);

    void removeBoard(long boardId);

    List<Long> getBoardIdByUser_id(long userId);

    Board getBoardByBoardId(long boardId);

    void editBoard(Board board);

    void updateViewCount(long boardId);

    List<Board> findByUser_id(long userId);
}
