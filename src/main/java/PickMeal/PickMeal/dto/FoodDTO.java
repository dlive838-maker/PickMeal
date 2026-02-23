package PickMeal.PickMeal.dto;

import lombok.Data;

@Data
public class FoodDTO {
    private int foodId;
    private String foodName;
    private String category;
    private String description;
    private String imagePath;
}
