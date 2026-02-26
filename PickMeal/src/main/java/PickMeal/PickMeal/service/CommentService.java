package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.Comment;
import PickMeal.PickMeal.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;

    public List<Comment> getCommentsByBoardId(long boardId) {
        return commentMapper.getCommentsByBoardId(boardId);
    }

    public void writeComment(Comment comment) {
        commentMapper.writeComment(comment);
    }
}
