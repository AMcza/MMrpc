package com.rpc.registry.factory;

import com.rpc.registry.EtcdRegistry;
import com.rpc.registry.Registry;
import com.rpc.spi.SpiLoader;

public class RegistryFactory {

   static {
       SpiLoader.load(Registry.class);
   }

    /**
     * 默认注册中心,默认(Etcd)
      */
   private static final Registry DEFAULT_REGISTRY=new EtcdRegistry();

   public static Registry getInstance(String key) {
       return SpiLoader.getInstance(Registry.class,key);
   }
}
