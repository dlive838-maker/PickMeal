package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.dto.PlaceStatsDto;
import PickMeal.PickMeal.service.PlaceStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/hotplace")
@RequiredArgsConstructor
public class HotPlaceController {

    private final PlaceStatsService placeStatsService;

    @GetMapping("")
    public String hotPlacePage() {
        return "hotplace";
    }

    @PostMapping("/stats")
    @ResponseBody
    public List<PlaceStatsDto> getStats(@RequestBody List<String> placeIds, Principal principal) {
        // 로그인 상태면 ID 전달, 아니면 null 전달
        String userId = (principal != null) ? principal.getName() : null;
        return placeStatsService.getStatsByKakaoIds(placeIds, userId);
    }

    @PostMapping("/view/{kakaoPlaceId}")
    @ResponseBody
    public ResponseEntity<?> addView(@PathVariable("kakaoPlaceId") String kakaoPlaceId, Principal principal) {
        // 로그인했으면 실제 ID, 안했으면 "guest"로 설정
        String userId = (principal != null) ? principal.getName() : "guest";

        placeStatsService.addViewLog(kakaoPlaceId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/heart/{kakaoPlaceId}")
    @ResponseBody
    public ResponseEntity<?> toggleHeart(@PathVariable("kakaoPlaceId") String kakaoPlaceId, Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        placeStatsService.toggleHeart(kakaoPlaceId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reviews/{kakaoPlaceId}")
    @ResponseBody
    public List<PlaceStatsDto> getReviews(@PathVariable String kakaoPlaceId) {
        return placeStatsService.getReviews(kakaoPlaceId);
    }

    @PostMapping("/reviews/{kakaoPlaceId}")
    @ResponseBody
    public ResponseEntity<?> addReview(@PathVariable String kakaoPlaceId, @RequestBody java.util.Map<String, Object> data, Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // JSON으로 넘어온 데이터 분리
        String content = (String) data.get("content");
        int rating = Integer.parseInt(data.get("rating").toString());

        placeStatsService.addReview(kakaoPlaceId, principal.getName(), content, rating);
        return ResponseEntity.ok().build();
    }

    // HotPlaceController.java 에 아래 메서드들을 추가하세요.

    @PostMapping("/reviews/update/{reviewId}")
    @ResponseBody
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody java.util.Map<String, Object> data, Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String content = (String) data.get("content");
        int rating = Integer.parseInt(data.get("rating").toString());
        String userId = principal.getName();

        boolean isUpdated = placeStatsService.updateReview(reviewId, userId, content, rating);

        if (!isUpdated) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reviews/delete/{reviewId}")
    @ResponseBody
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // 서비스의 deleteReview 호출
        placeStatsService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
}