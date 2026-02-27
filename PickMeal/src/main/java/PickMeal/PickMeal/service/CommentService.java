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

    public void deleteComment(long comment_id) {
        commentMapper.deleteComment(comment_id);
    }

    public Comment getCommentByComment_id(long comment_id) {
        return commentMapper.getCommentByCommentID(comment_id);
    }

    public void updateComment(long comment_id, String content) {
        commentMapper.updateComment(comment_id, content);
    }
}
