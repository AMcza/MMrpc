package com.rpc.registry;

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
