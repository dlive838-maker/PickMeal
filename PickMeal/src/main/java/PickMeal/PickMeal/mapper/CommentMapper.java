package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> getCommentsByBoardId(long boardId);

    void writeComment(Comment comment);
}
