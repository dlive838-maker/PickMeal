package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.service.OAuthService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/login/oauth2/code")
public class OAuthController {

    private final OAuthService oAuthService;

    @ResponseBody
    @GetMapping("/kakao")
    public void KakaoCallback(@RequestParam String code){

        oAuthService.getKakaoUserInfo(oAuthService.getKakaoAccessToken(code));

    }
}