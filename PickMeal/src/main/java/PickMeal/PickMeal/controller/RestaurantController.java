package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.dto.ReviewWishDTO;
import PickMeal.PickMeal.mapper.RestaurantMapper;
import PickMeal.PickMeal.mapper.ReviewWishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // 세션 대신 인증 정보 사용
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RestaurantController {

    @Autowired
    private RestaurantMapper restaurantMapper;

    @Autowired
    private ReviewWishMapper reviewWishMapper;

    // [중요] UserController에 있는 ID 추출 로직을 그대로 가져왔습니다.
    private String getLoginUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        return authentication.getName(); // 현재 로그인한 사람의 ID를 가져옵니다.
    }

    @PostMapping("/restaurant/view/{resId}")
    public void increaseView(@PathVariable Long resId) {
        restaurantMapper.updateViewCount(resId);
    }

    @PostMapping("/review/save")
    public void saveInteraction(@RequestBody ReviewWishDTO dto, Authentication authentication) {
        String userId = getLoginUserId(authentication); // 인증 객체에서 ID 추출

        if (userId == null) return; // 로그인이 안 되어 있으면 저장 중단 (null 방어)

        //dto.setUserId(userId);
        if (dto.getContent() != null && !dto.getContent().isEmpty()) {
            dto.setWish(true);
        }
        reviewWishMapper.saveInteraction(dto);
    }

    @PostMapping("/wishlist/{resId}")
    public void toggleWish(@PathVariable Long resId, Authentication authentication) {
        String userId = getLoginUserId(authentication); // 인증 객체에서 ID 추출

        if (userId == null) return; // null 방어

        ReviewWishDTO dto = new ReviewWishDTO();
        dto.setResId(resId);
        //dto.setUserId(userId);
        dto.setWish(true);
        reviewWishMapper.saveInteraction(dto);
    }
}