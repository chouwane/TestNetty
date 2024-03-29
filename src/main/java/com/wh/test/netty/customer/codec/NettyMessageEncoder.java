package com.wh.test.netty.customer.codec;

import com.wh.test.netty.customer.marshalling.MarshallingEncoder;
import com.wh.test.netty.customer.msg.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghui on 17-11-9.
 */
public final class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage>{

    MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder() throws IOException {
        marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception {

        if(msg == null || msg.getHeader() == null){
            throw new Exception("The encoder msg is null");
        }

        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt(msg.getHeader().getCrcCode());
        sendBuf.writeInt(msg.getHeader().getLength());
        sendBuf.writeLong(msg.getHeader().getSessionId());
        sendBuf.writeByte(msg.getHeader().getType());
        sendBuf.writeByte(msg.getHeader().getPriority());
        sendBuf.writeInt(msg.getHeader().getAttachment().size());

        String key = null;
        byte[] keyArray = null;
        Object value = null;

        for(Map.Entry<String, Object> entry : msg.getHeader().getAttachment().entrySet()){

            key = entry.getKey();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);

            value = entry.getValue();
            marshallingEncoder.encode(value, sendBuf);

        }


        key = null;
        keyArray = null;
        value = null;

        if(msg.getBody() != null){

            marshallingEncoder.encode(msg.getBody(), sendBuf);

        }else {
            sendBuf.writeInt(0);
        }

        sendBuf.setInt(4, sendBuf.readableBytes());

        out.add(sendBuf);

    }
}
