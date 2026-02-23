package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User findByid(String id);

    void save(User user);
}
