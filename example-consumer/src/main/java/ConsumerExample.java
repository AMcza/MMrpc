import com.rpc.RpcApplication;
import com.rpc.bootstrap.ConsumerBootstrap;
import com.rpc.proxy.ServiceProxyFactory;
import example.common.model.User;
import example.common.service.UserService;

public class ConsumerExample {

    public static void main(String[] args) {
        ConsumerBootstrap.init();

        //获取代理
        UserService userService= ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("cmm");

        //调用
        User newUser=userService.getUser(user);
        if(newUser!=null){
            System.out.println(newUser.getName());
        }else{
            System.out.println("调用失败");
        }
    }
}
