import cn.hutool.core.util.IdUtil;
import com.rpc.constant.ProtocolConstant;
import com.rpc.constant.RpcConstant;
import com.rpc.enums.ProtocolMessageSerializerEnum;
import com.rpc.enums.ProtocolMessageStatusEnum;
import com.rpc.enums.ProtocolMessageTypeEnum;
import com.rpc.model.RpcRequest;
import com.rpc.protocol.ProtocolMessage;
import com.rpc.protocol.ProtocolMessageDecoder;
import com.rpc.protocol.ProtocolMessageEncoder;
import io.vertx.core.buffer.Buffer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class testMessageTest {

    @Test
    public void test() throws IOException {
        //构建消息
        ProtocolMessage<RpcRequest> protocolMessage=new ProtocolMessage<>();
        ProtocolMessage.Header header=new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(0);
        RpcRequest rpcRequest=new RpcRequest();
        rpcRequest.setServiceName("com.rpc.test.TestService");
        rpcRequest.setMethodName("test");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setArgs(new Object[]{"hello world"});

        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);

        Buffer encodeBuffer= ProtocolMessageEncoder.encode(protocolMessage);
        ProtocolMessage<?> message= ProtocolMessageDecoder.decode(encodeBuffer);

        Assert.assertNotNull(message);
    }
}
