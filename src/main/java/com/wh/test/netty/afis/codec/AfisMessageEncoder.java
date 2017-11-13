package com.wh.test.netty.afis.codec;

import com.afis.net.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by wanghui on 17-11-11.
 */
public class AfisMessageEncoder extends MessageToMessageEncoder<Message> {

    private final AfisMessagePacket packet = new AfisMessagePacket();

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {

        if(msg == null){
            throw new Exception("The encoder msg is null");
        }

        ByteBuf sendBuf = packet.packet(msg);
        out.add(sendBuf);
    }
}
