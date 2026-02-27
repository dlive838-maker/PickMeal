package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.Game;
import PickMeal.PickMeal.mapper.GameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {
    private final GameMapper gameMapper;

    public void insertGameRecord(Game game) {
        // [핵심 로직 추가]
        // 1. user_id가 0이면 외래키 제약조건 위반이 발생하므로 null로 변경합니다.
        if (game.getUser_id() != null && game.getUser_id() == 0L) {
            System.out.println("--- [GameService] user_id가 0이라서 null로 세탁합니다 ---");
            game.setUser_id(null);
        }

        // 2. admin_id도 마찬가지로 0이면 null로 처리합니다.
        if (game.getAdmin_id() != null && game.getAdmin_id() == 0L) {
            System.out.println("--- [GameService] admin_id가 0이라서 null로 세탁합니다 ---");
            game.setAdmin_id(null);
        }

        // 이제 깨끗해진 데이터로 DB에 저장합니다.
        gameMapper.insertGameRecord(game);
    }
}