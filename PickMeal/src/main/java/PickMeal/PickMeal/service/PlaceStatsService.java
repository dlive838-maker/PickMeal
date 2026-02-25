package PickMeal.PickMeal.service;

import PickMeal.PickMeal.dto.PlaceStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service // Spring Bean으로 등록하여 컨트롤러에서 주입받을 수 있게 함
@RequiredArgsConstructor // JdbcTemplate 생성자 주입을 위해 사용
public class PlaceStatsService {

    private final JdbcTemplate jdbcTemplate;

    public List<PlaceStatsDto> getStatsByKakaoIds(List<String> placeIds) {
        if (placeIds == null || placeIds.isEmpty()) {
            return Collections.emptyList();
        }

        // SQL: 각 테이블(조회, 찜, 리뷰)에서 kakao_place_id별로 갯수를 집계함
        // IN 절을 사용하여 전달받은 ID 리스트에 해당하는 데이터만 추출
        String inSql = String.join(",", Collections.nCopies(placeIds.size(), "?"));

        String sql = "SELECT p.kakao_place_id, " +
                " (SELECT COUNT(*) FROM view_logs WHERE kakao_place_id = p.kakao_place_id) as view_count, " +
                " (SELECT COUNT(*) FROM hearts WHERE kakao_place_id = p.kakao_place_id) as heart_count, " +
                " (SELECT COUNT(*) FROM reviews WHERE kakao_place_id = p.kakao_place_id) as review_count " +
                "FROM (SELECT UNNEST(ARRAY[" + inSql + "])) as p(kakao_place_id)";
        // 주의: 위 SQL은 예시이며 실제 DB 스키마 구조에 따라 JOIN 등으로 최적화가 필요함

        // 임시 데이터 반환 (DB 연결 전 테스트용)
        // 실제로는 위 SQL 결과를 mapping하여 리턴해야 함
        return placeIds.stream().map(id -> {
            PlaceStatsDto dto = new PlaceStatsDto();
            dto.setKakaoPlaceId(id);
            dto.setViewCount((int)(Math.random() * 100)); // 테스트용 랜덤 값
            dto.setHeartCount((int)(Math.random() * 50));
            dto.setReviewCount((int)(Math.random() * 30));
            return dto;
        }).collect(Collectors.toList());
    }

    public void addViewLog(String kakaoPlaceId) {
    }
}