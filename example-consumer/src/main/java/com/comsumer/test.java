package com.comsumer;

import com.rpc.config.RpcConfig;
import com.rpc.utils.ConfigUtils;

public class test {
    public static void main(String[] args) {
        RpcConfig rpc= ConfigUtils.loadConfig(RpcConfig.class,"rpc");
        System.out.println(rpc);
    }
}
