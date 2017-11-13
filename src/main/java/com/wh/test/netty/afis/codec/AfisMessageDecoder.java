package com.wh.test.netty.afis.codec;

import com.afis.net.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by wanghui on 17-11-11.
 */
public class AfisMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final AfisMessagePacket packet = new AfisMessagePacket();
    private byte[] remainingBytes;//保存不完整的消息

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

        ByteBuf currBB = null;
        if(remainingBytes == null) {
            currBB = msg;
        }else {
            byte[] tb = new byte[remainingBytes.length + msg.readableBytes()];
            System.arraycopy(remainingBytes, 0, tb, 0, remainingBytes.length);
            byte[] vb = new byte[msg.readableBytes()];
            msg.readBytes(vb);
            System.arraycopy(vb, 0, tb, remainingBytes.length, vb.length);
            currBB = Unpooled.copiedBuffer(tb);
        }
        while(currBB.readableBytes() > 0) {
            if(!doDecode(ctx, currBB, out)) {
                break;
            }
        }

        if(currBB.readableBytes() > 0) {
            remainingBytes = new byte[currBB.readableBytes()];
            currBB.readBytes(remainingBytes);
        }else {
            remainingBytes = null;
        }

    }

    /**
     *  解码出一条完整的消息
     * @param ctx
     * @param msg
     * @param out
     * @return
     */
    private boolean doDecode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {

        if(msg.readableBytes() > 0) {
            try {

                msg.markReaderIndex();
                Message message = packet.unPack(msg);
                if(message != null) {
                    out.add(message);
                }else {
                    msg.resetReaderIndex();
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                msg.resetReaderIndex();
                return false;
            }
        }

        return true;
    }

}
