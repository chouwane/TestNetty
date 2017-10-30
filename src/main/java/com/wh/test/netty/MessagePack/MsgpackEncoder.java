package com.wh.test.netty.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * msgpack编码器
 * Created by wanghui on 17-10-28.
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf byteBuf) throws Exception {

        MessagePack msgpack = new MessagePack();
        byte[] raw = msgpack.write(o);
        byteBuf.writeBytes(raw);

    }
}
