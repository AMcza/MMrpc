package com.rpc.server.tcp;

import com.rpc.constant.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        this.recordParser =initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler){
        //构造parser
        RecordParser parser=RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            //初始化
            int size=-1;
            //一次完整的读取(头+体)
            Buffer resultBuffer=Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if(size==-1){
                    //读取消息体长度
                    size=buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    //写入头信息到结果
                    resultBuffer.appendBuffer(buffer);
                }else{
                    //写入体信息到结果
                    resultBuffer.appendBuffer(buffer);
                    //已拼接为完整
                    bufferHandler.handle(resultBuffer);
                    //重置一轮
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    size=-1;
                    resultBuffer=Buffer.buffer();
                }
            }
        });
        return parser;
    }
}
