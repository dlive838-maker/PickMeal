package PickMeal.PickMeal.domain;

public class Food {
    private Long foodId;
    private String foodName;
    private String category;
    private String description;
    private String imagePath;

    public Food(Long foodId, String foodName, String category, String description, String imagePath) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.category = category;
        this.description = description;
        this.imagePath = imagePath;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
