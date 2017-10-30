package com.wh.test.netty.protobuf.customer;

import com.google.protobuf.MessageLite;
import com.wh.test.netty.protobuf.SubscribeReqProto;
import com.wh.test.netty.protobuf.SubscribeRespProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义protobuf编码器
 *
 * @author wanghui
 * @date 17-10-30
 */
public class CustomProtobufEncoder extends MessageToByteEncoder<MessageLite> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf out) throws Exception {


        byte[] body = msg.toByteArray();
        byte[] header = encodeHeader(msg, (short)body.length);

        out.writeBytes(header);
        out.writeBytes(body);

        return;
    }

    private byte[] encodeHeader(MessageLite msg, short bodyLength) {
        byte messageType = 0x0f;

        if (msg instanceof SubscribeReqProto.SubscribeReq) {
            messageType = 0x00;
        } else if (msg instanceof SubscribeRespProto.SubscribeResp) {
            messageType = 0x01;
        }

        byte[] header = new byte[4];
        header[0] = (byte) (bodyLength & 0xff);
        header[1] = (byte) ((bodyLength >> 8) & 0xff);
        header[2] = 0; // 保留字段
        header[3] = messageType;

        return header;

    }
}
