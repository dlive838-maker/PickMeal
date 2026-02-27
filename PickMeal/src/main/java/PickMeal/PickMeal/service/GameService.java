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
        gameMapper.insertGameRecord(game);
    }
}