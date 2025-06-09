package com.example.provider;

import com.rpc.bootstrap.ProviderBootstrap;
import com.rpc.model.ServiceRegisterInfo;
import example.common.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class ProviderExample {
    public static void main(String[] args) {
        //启动服务
        List<ServiceRegisterInfo> serviceRegisterInfoList=new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo=new ServiceRegisterInfo(UserService.class.getName(),UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        //服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
