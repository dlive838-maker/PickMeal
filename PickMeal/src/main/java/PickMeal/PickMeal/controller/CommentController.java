package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.Comment;
import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.service.CommentService;
import PickMeal.PickMeal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    private final UserService userService;

    @PostMapping("/write/{boardId}")
    public String writeComment(@PathVariable long boardId, Comment comment, Authentication authentication) {
        User user = userService.getAuthenticatedUser(authentication);

        if (user != null) {
            comment.setBoardId(boardId);
            comment.setUser_id(user.getUser_id());
            commentService.writeComment(comment);
        }
        return "redirect:/board/detail/" + boardId;
    }

    @PostMapping("/delete/{comment_id}")
    public String deleteComment(@PathVariable long comment_id, Authentication authentication) {
        Comment comment = commentService.getCommentByComment_id(comment_id);
        User user = userService.getAuthenticatedUser(authentication);

        // 로그인한 유저와 댓글 작성자가 같은지 확인하는 로직이 필요합니다!
        if (user != null && comment.getUser_id() == user.getUser_id()) {
            commentService.deleteComment(comment_id);
        }
        return "redirect:/board/detail/" + comment.getBoardId();
    }



    @PostMapping("/update/{comment_id}")
    public String updateComment(@PathVariable long comment_id, String content) {
        long boardId = commentService.getCommentByComment_id(comment_id).getBoardId();
        commentService.updateComment(comment_id, content);
        return "redirect:/board/detail/" + boardId;
    }
}
