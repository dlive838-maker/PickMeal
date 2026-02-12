package PickMeal.PickMeal.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class User {
    private long user_id;
    private String id;
    private String password;
    private String nickname;
    private String name;
    private String email;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;
    private String gender;
    private String phoneNumber;
    private Date joinDate;
    private String socialLoginSite;
    private String socialId;
}
