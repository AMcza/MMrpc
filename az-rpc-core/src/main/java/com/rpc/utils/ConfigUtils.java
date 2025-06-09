package com.rpc.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;
import com.rpc.config.RpcConfig;
import com.rpc.constant.RpcConstant;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 配置工具类
 */
public class ConfigUtils {
    /**
     * 加载配置对象(用户调用)
     * @param tClass
     * @param prefix
     * @return
     * @param <T>
     */
     public static <T> T loadConfig(Class<T> tClass,String prefix){
         return loadConfig(tClass,prefix,"");
     }

    /**
     * 加载配置对象,支持区分环境
     * @param tClass
     * @param prefix
     * @param environment
     * @return
     * @param <T>
     */
     public static <T> T loadConfig(Class<T> tClass,String prefix,String environment) {
         //构建配置文件的基础名称
         StringBuilder configFileBuilder=new StringBuilder("application");
         if(StrUtil.isNotBlank(environment)){
             configFileBuilder.append("-").append(environment);
         }
         String propertiesConfig=configFileBuilder.toString()+RpcConstant.DEFAULT_CONFIG_SUFFIX;
         String ymlConfig=configFileBuilder.toString()+ RpcConstant.YML_CONFIG_SUFFIX;
         String yamlConfig=configFileBuilder.toString()+RpcConstant.YAML_CONFIG_SUFFIX;

         //加载配置文件优先yml
         InputStream inputStream=ConfigUtils.class.getClassLoader().getResourceAsStream(ymlConfig);
         if(inputStream!=null){
             System.out.println("加载yml");
             return LoadYamlAndYml(inputStream, prefix, tClass);
         }
         inputStream=ConfigUtils.class.getClassLoader().getResourceAsStream(yamlConfig);
         if(inputStream!=null){
             System.out.println("加载yaml");
             return LoadYamlAndYml2(inputStream, prefix, tClass);
         }
         System.out.println("加载properties");
         //加载properties配置文件
         Props props= new Props(propertiesConfig);
         return props.toBean(tClass, prefix);
     }


    /**
     * 加载yaml和yml文件
     * @param inputStream
     * @param prefix
     * @param tClass
     * @return
     * @param <T>
     */
     private static <T>T LoadYamlAndYml(InputStream inputStream,String prefix,Class<T> tClass) {
         Yaml yaml=new Yaml();
         Map<String,Object> map=yaml.load(inputStream);
         Map<String,Object> serverConfigMap=(Map<String, Object>) map.get(prefix);
         return BeanUtil.toBean(serverConfigMap, tClass);
     }

    /**
     * 加载yaml和yml文件 使用hutool
     * @param inputStream
     * @param prefix
     * @param tClass
     * @return
     * @param <T>
     */
    private static <T>T LoadYamlAndYml2(InputStream inputStream,String prefix,Class<T> tClass) {
        Map<String,Object> map=YamlUtil.load(inputStream, Map.class);
        Map<String,Object> serverConfigMap=(Map<String, Object>) map.get(prefix);
        return BeanUtil.toBean(serverConfigMap, tClass);
    }
}
