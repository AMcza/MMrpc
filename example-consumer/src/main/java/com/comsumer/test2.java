package com.comsumer;

import com.rpc.proxy.factory.ServiceProxyTcpFactory;
import example.common.model.User;
import example.common.service.UserService;

public class test2 {
    public static void main(String[] args) {
        UserService userService= ServiceProxyTcpFactory.getProxy(UserService.class);
        User user=new User();
        user.setName("da");
        User newUser=userService.getUser(user);
        if(user!=null){
            System.out.println(newUser);
        }else{
            System.out.println("null");
        }
        long number=userService.getNumber();
        System.out.println(number);
    }
}
