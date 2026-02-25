package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.dto.PlaceStatsDto;
import PickMeal.PickMeal.service.PlaceStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/hotplace")
@RequiredArgsConstructor
public class HotPlaceController {

    private final PlaceStatsService placeStatsService;

    // 핫플레이스 페이지 이동
    @GetMapping("")
    public String hotPlacePage() {
        return "hotplace";
    }

    // [추가] 장소들 통계 가져오기 (API)
    @PostMapping("/stats")
    @ResponseBody
    public List<PlaceStatsDto> getStats(@RequestBody List<String> placeIds) {
        return placeStatsService.getStatsByKakaoIds(placeIds);
    }

    // [추가] 가게 클릭 시 조회수 1 증가 (API)
    @PostMapping("/view")
    @ResponseBody
    public void addView(@RequestParam String kakaoPlaceId) {
        placeStatsService.addViewLog(kakaoPlaceId);
    }
}