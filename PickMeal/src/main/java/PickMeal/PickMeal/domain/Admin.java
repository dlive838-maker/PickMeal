package PickMeal.PickMeal.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Admin {
    private Long adminId;
    private String loginId;
    private String password;
    private String name;
    private String role;
    private LocalDateTime createdAt; //
}