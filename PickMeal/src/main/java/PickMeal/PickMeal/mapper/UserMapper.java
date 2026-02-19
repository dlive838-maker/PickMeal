package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findById(String id);

    void save(User user);

    void edit(User user);

    void updateStatus(@Param("user_id") Long user_id, @Param("status") String status);

    int countByNickname(String nickname);
}
