package com.example.provider;

import example.common.model.User;
import example.common.service.UserService;

public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名"+user.getName());
        return user;
    }
}
