package PickMeal.PickMeal.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private long comment_id;
    private long boardId;
    private long user_id;
    private String content;
    private Date createCommentDate;
    private Date updateCommentDate;
    private String nickname;
}
