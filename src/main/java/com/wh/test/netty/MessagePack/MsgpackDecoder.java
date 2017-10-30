package com.wh.test.netty.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * msgpack解码器
 * Created by wanghui on 17-10-28.
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        final int length = byteBuf.readableBytes();
        final byte[] array = new byte[length];
        byteBuf.getBytes(byteBuf.readerIndex(), array, 0, length);
        MessagePack msgpack = new MessagePack();
        list.add(msgpack.read(array));

    }
}
