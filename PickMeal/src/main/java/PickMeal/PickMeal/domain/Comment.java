package PickMeal.PickMeal.domain;

import lombok.Data;

@Data
public class Comment {
    private long comment_id;
    private long boardId;
    private long user_id;
    private String content;
    private java.util.Date createCommentDate;
    private java.util.Date updateCommentDate;
    private String nickname;
}
