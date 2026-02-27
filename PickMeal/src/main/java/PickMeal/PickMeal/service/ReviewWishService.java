package PickMeal.PickMeal.service;

import PickMeal.PickMeal.dto.RestaurantDTO;
import PickMeal.PickMeal.mapper.ReviewWishMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewWishService {
    private final ReviewWishMapper reviewWishMapper;


    public List<RestaurantDTO> getPopularRest() {
        return reviewWishMapper.getPopularRest();
    }
}
