package PickMeal.PickMeal.domain;

import lombok.Data;

@Data
public class File {

    private long boardId;
    private String filePath;
    private String originalName;
    private long fileId;
    private String storedName;


}
