package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.Board;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<Board> showBoard();

    void writeBoard(Board board);

    void removeBoard();
}
