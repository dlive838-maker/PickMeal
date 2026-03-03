package PickMeal.PickMeal.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final String name; // DB의 실명을 담을 공간

    public CustomUserDetails(String username, String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String name) {
        super(username, password, authorities); // ID, PW, 권한은 부모(User)에게 전달
        this.name = name; // 이름은 내 공간에 저장
    }

    // 타임리프 등에서 호출하기 위한 게터(Getter)
    public String getName() {
        return name;
    }
}