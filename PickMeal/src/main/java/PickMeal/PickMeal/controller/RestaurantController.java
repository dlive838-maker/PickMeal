package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.dto.PlaceStatsDto;
import PickMeal.PickMeal.dto.ReviewWishDTO;
import PickMeal.PickMeal.mapper.RestaurantMapper;
import PickMeal.PickMeal.mapper.ReviewWishMapper;
import PickMeal.PickMeal.service.PlaceStatsService;
import PickMeal.PickMeal.service.RestaurantService;
import PickMeal.PickMeal.service.ReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class RestaurantController {

    private final RestaurantMapper restaurantMapper;
    private final ReviewWishMapper reviewWishMapper;
    private final RestaurantService restaurantService;
    private final ReviewService reviewService;
    private final PlaceStatsService placeStatsService; // [추가] 서비스 필드

    // [수정] 생성자에서 placeStatsService를 반드시 초기화해야 합니다.
    public RestaurantController(RestaurantMapper restaurantMapper,
                                ReviewWishMapper reviewWishMapper,
                                RestaurantService restaurantService,
                                ReviewService reviewService,
                                PlaceStatsService placeStatsService) {
        this.restaurantMapper = restaurantMapper;
        this.reviewWishMapper = reviewWishMapper;
        this.restaurantService = restaurantService;
        this.reviewService = reviewService;
        this.placeStatsService = placeStatsService;
    }

    // 로그인 유저의 문자열 ID(예: woals106)를 가져오는 메서드
    private String getLoginUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        return authentication.getName();
    }

    // 1. 맛집 탐지기 페이지 접속
    @GetMapping("/meal-spotter")
    public String mealSpotter(Model model, Authentication authentication) {

        List<PlaceStatsDto> popularPlaceList = placeStatsService.getPopularPlace();

        String userId = getLoginUserId(authentication);

        if (userId != null && popularPlaceList != null) {
            for (PlaceStatsDto dto : popularPlaceList) {
                Long resId = Long.parseLong(dto.getKakaoPlaceId());

                // [중요] userId가 문자열이므로 Long.valueOf를 제거했습니다.
                // 매퍼(checkWish)의 파라미터 타입이 String/Varchar 인지 확인하세요.
                int check = reviewWishMapper.checkWish(userId, resId);
                dto.setLiked(check > 0);
            }
        }

        model.addAttribute("popularPlaceList", popularPlaceList);
        return "board/meal-spotter";
    }

    // 3. 리뷰 저장 API
    @PostMapping("/api/review/save")
    @ResponseBody
    public int saveReview(@RequestBody ReviewWishDTO dto, Authentication auth) {
        String userId = getLoginUserId(auth);

        if (userId != null) {
            dto.setUserId(userId);
        }

        // 1. 리뷰 저장 실행
        reviewService.save(dto);

        // 2. [수정] dto.getResId()가 String일 경우를 대비해 타입을 체크합니다.
        // 만약 getReviewCount가 Long을 받는다면 Long.parseLong을 써야 합니다.
        try {
            // String인 resId를 숫자로 변환하여 매퍼에 전달
            Long resIdLong = Long.parseLong(dto.getResId());
            return reviewWishMapper.getReviewCount(resIdLong);
        } catch (Exception e) {
            System.out.println("리뷰 개수 조회 중 에러 발생: " + e.getMessage());
            return 0; // 에러 시 기본값 반환
        }
    }

    // 찜하기 토글 API
    @PostMapping("/api/wishlist/{resId}")
    @ResponseBody
    public String toggleWish(@PathVariable Long resId, Authentication authentication) {
        String userId = getLoginUserId(authentication);

        if (userId == null) return "fail";

        // [수정] 문자열 ID를 그대로 사용하여 찜 상태 확인
        int alreadyWished = reviewWishMapper.checkWish(userId, resId);

        if (alreadyWished > 0) {
            reviewWishMapper.deleteWish(userId, resId);
        } else {
            reviewWishMapper.insertWish(resId, userId);
        }

        return String.valueOf(reviewWishMapper.getTotalWishCount(resId));
    }

    @GetMapping("/api/reviews/{resId}")
    @ResponseBody
    public List<ReviewWishDTO> getReviewList(@PathVariable Long resId) {
        return reviewWishMapper.getReviewsByRestaurant(resId);
    }

    @DeleteMapping("/api/review/delete/{reviewId}")
    @ResponseBody
    public String deleteReview(@PathVariable Long reviewId) {
        reviewWishMapper.deleteReview(reviewId);
        return "success";
    }
}