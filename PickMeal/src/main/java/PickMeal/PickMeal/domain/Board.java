package PickMeal.PickMeal.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Board {
    private long boardId;
    private long user_id;
    private String title;
    private String content;
    private Date createDate;
    private Date updateDate;
    private long likeCount;
    private long dislikeCount;
    private long viewCount;
}
