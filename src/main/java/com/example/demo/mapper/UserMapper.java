package com.example.demo.mapper;

import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User fromCreate(UserCreateRequest req) {
        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        return u;
    }
    public User merge(User u, UserUpdateRequest req) {
        if (req.getName() != null && !req.getName().isBlank()) u.setName(req.getName());
        if (req.getEmail() != null && !req.getEmail().isBlank()) u.setEmail(req.getEmail());
        return u;
    }
    public UserDto toDto(User u) {
        UserDto d = new UserDto();
        d.setId(u.getId());
        d.setName(u.getName());
        d.setEmail(u.getEmail());
        return d;
    }
}
