package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.Board;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {
    void showBoard();

    void writeBoard(Board board);

    void removeBoard();
}
