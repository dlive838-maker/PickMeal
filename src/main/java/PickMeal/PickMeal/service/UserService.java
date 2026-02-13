package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserMapper userMapper;


    public void save(User user){

        if(isDuplicateUser(user.getId())){
            System.out.println("2");
        }else {
            userMapper.save(user);
            System.out.println("1");
        }

    }

    public boolean isDuplicateUser(String id) {
        if(userMapper.findByid(id) != null){
            return true;
        }else {
            return false;
        }
    }

    public User findByid(String id){
        return userMapper.findByid(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByid(username);
        if (user == null) {
            throw new UsernameNotFoundException("존재하지 않는 유저: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getId(),
                user.getPassword(),
                Collections.emptyList() // 권한 목록 (필요 시 추가)
        );
    }
}