package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {

        return "login";
    }

    @GetMapping("/signup")
    public String signUp() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute User user){
        System.out.println(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        System.out.println(user);
        return "redirect:/login";
    }

    @GetMapping("/kakao/login")
    public String kakaoLogin() {


        return "redirect:/login";
    }


    @PostMapping("kakao/signup")
    public String KakaoSignUP() {



        return "login";
    }
}