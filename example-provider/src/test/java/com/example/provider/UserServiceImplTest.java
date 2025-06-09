package com.example.provider;

import com.rpc.config.RpcConfig;
import com.rpc.utils.ConfigUtils;
import junit.framework.TestCase;

public class UserServiceImplTest extends TestCase {
        public static void main(String[] args) {
                RpcConfig rpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
                System.out.println(rpcConfig);

        }
}