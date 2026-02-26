package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.Comment;
import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/write/{boardId}")
    public String writeComment(@PathVariable long boardId, Comment comment, @AuthenticationPrincipal User user) {
        comment.setBoardId(boardId);
        comment.setUser_id(user.getUser_id());
        commentService.writeComment(comment);
        return "redirect:/board/detail/" + boardId;
    }

    @PostMapping("/delete/{comment_id}")
    public String deleteComment(@PathVariable long comment_id) {
        long boardId = commentService.getCommentByComment_id(comment_id).getBoardId();

        commentService.deleteComment(comment_id);

        return "redirect:/board/detail/" + boardId;
    }



    @PostMapping("/update/{comment_id}")
    public String updateComment(@PathVariable long comment_id, String content) {
        long boardId = commentService.getCommentByComment_id(comment_id).getBoardId();
        commentService.updateComment(comment_id, content);
        return "redirect:/board/detail/" + boardId;
    }
}
