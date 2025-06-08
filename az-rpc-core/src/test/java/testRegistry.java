import com.rpc.model.ServiceMetaInfo;
import com.rpc.registry.EtcdRegistry;
import com.rpc.registry.Registry;
import com.rpc.config.RegistryConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class testRegistry {
    final Registry registry=new EtcdRegistry();

    @Before
    public void init(){
        RegistryConfig registryConfig=new RegistryConfig();
        registryConfig.setAddress("http://localhost:2379");
        registry.init(registryConfig);
    }
    //注册
    @Test
    public void registry() throws Exception{
        ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.register(serviceMetaInfo);
        serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1235);
        registry.register(serviceMetaInfo);
    }
    //取消注册
    @Test
    public void unRegistry(){
        ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        serviceMetaInfo.setServiceHost("localhost");
        serviceMetaInfo.setServicePort(1234);
        registry.unRegister(serviceMetaInfo);
    }
    //发现服务
    @Test
    public void serviceDiscovery(){
        ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceVersion("myService");
        serviceMetaInfo.setServiceVersion("1.0");
        String serviceKey=serviceMetaInfo.getServiceKey();
        List<ServiceMetaInfo> serviceMetaInfoList=registry.serviceDiscovery(serviceKey);
        Assert.assertNotNull(serviceMetaInfoList);
    }
}
