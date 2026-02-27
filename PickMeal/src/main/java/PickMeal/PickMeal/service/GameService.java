package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.Food;
import PickMeal.PickMeal.domain.Game;
import PickMeal.PickMeal.mapper.GameMapper;
import PickMeal.PickMeal.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final GameMapper gameMapper;
    private final UserMapper userMapper;

    /**
     * 회원의 선호 음식을 무조건 포함하고, 비선호 음식을 제외하여 월드컵 리스트를 만듭니다.
     * @param userId 로그인 유저 PK
     * @param allFoods 전체 음식 리스트 (주로 120여 개)
     * @param round 게임 강수 (예: 16)
     */
    public List<Food> getPriorityFoodList(Long userId, List<Food> allFoods, int round) {

        // 1. 비회원이면 전체 메뉴 중 무작위로 섞어서 강수만큼만 반환합니다.
        if (userId == null) {
            Collections.shuffle(allFoods); // [비유] 카드를 무작위로 섞음
            return allFoods.subList(0, Math.min(round, allFoods.size()));
        }

        // 2. DB에서 회원의 선호 음식("짜장면, 돈까스")과 비선호 음식("생선") 정보를 가져옵니다.
        String likedStr = userMapper.getLikedMenuString(userId);
        String dislikedStr = userMapper.getDislikedMenuString(userId);

        // 쉼표(,) 기준으로 잘라서 리스트로 만듭니다. (공백 제거 포함)
        List<String> likedList = parseMenuList(likedStr);
        List<String> dislikedList = parseMenuList(dislikedStr);

        // 3. [우선 순위 1] 좋아하는 음식(짜장면, 돈까스 등)을 먼저 바구니에 담습니다.
        List<Food> selectedFoods = allFoods.stream()
                .filter(food -> likedList.contains(food.getFoodName()))
                .collect(Collectors.toList());

        // 4. [우선 순위 2] 싫어하는 음식과 이미 담은 선호 음식을 제외한 나머지를 준비합니다.
        List<Food> otherFoods = allFoods.stream()
                .filter(food -> !likedList.contains(food.getFoodName()) && !dislikedList.contains(food.getFoodName()))
                .collect(Collectors.toList());

        // 나머지 후보들을 무작위로 섞습니다.
        Collections.shuffle(otherFoods);

        // 5. 바구니에 부족한 개수만큼 무작위 음식들로 채웁니다. (예: 16강인데 선호가 2개면 14개 채움)
        int neededCount = round - selectedFoods.size();
        if (neededCount > 0 && !otherFoods.isEmpty()) {
            selectedFoods.addAll(otherFoods.subList(0, Math.min(neededCount, otherFoods.size())));
        }

        // 최종적으로 한 번 더 섞어서 게임판에 골고루 배치되게 합니다.
        Collections.shuffle(selectedFoods);
        return selectedFoods;
    }

    // 문자열을 쉼표 기준으로 쪼개주는 도구 메서드
    private List<String> parseMenuList(String menuStr) {
        if (menuStr == null || menuStr.isEmpty()) return new ArrayList<>();
        return Arrays.asList(menuStr.split("\\s*,\\s*"));
    }

    public void insertGameRecord(Game game) {
        gameMapper.insertGameRecord(game);
    }
}