package com.wh.test.netty.protobuf.customer;

import com.google.protobuf.MessageLite;
import com.wh.test.netty.protobuf.SubscribeReqProto;
import com.wh.test.netty.protobuf.SubscribeRespProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 自定义protobuf解码器
 *
 * 实现了2个功能，
 *          1）通过包头中的长度信息来解决半包和粘包。
 *          2）把消息body反序列化为对应的protobuf类型（根据包头中的类型信息）。

            其中的decodeBody方法具体的实现要根据你要传输哪些protobuf类型来修改代码，也可以稍加设计避免使用太多的if…else。
 *
 *
 * @author wanghui
 * @date 17-10-30
 */
public class CustomProtobufDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() > 4) { // 如果可读长度小于包头长度，退出。
            in.markReaderIndex();

            // 获取包头中的body长度
            byte low = in.readByte();
            byte high = in.readByte();
            short s0 = (short) (low & 0xff);
            short s1 = (short) (high & 0xff);
            s1 <<= 8;
            short length = (short) (s0 | s1);

            // 获取包头中的protobuf类型
            in.readByte();
            byte dataType = in.readByte();

            // 如果可读长度小于body长度，恢复读指针，退出。
            if (in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }

            // 读取body
            ByteBuf bodyByteBuf = in.readBytes(length);

            byte[] array;
            int offset;

            int readableLen= bodyByteBuf.readableBytes();
            if (bodyByteBuf.hasArray()) {
                array = bodyByteBuf.array();
                offset = bodyByteBuf.arrayOffset() + bodyByteBuf.readerIndex();
            } else {
                array = new byte[readableLen];
                bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                offset = 0;
            }

            //反序列化
            MessageLite result = decodeBody(dataType, array, offset, readableLen);
            out.add(result);
        }
    }

    public MessageLite decodeBody(byte dataType, byte[] array, int offset, int length) throws Exception {
        if (dataType == 0x00) {
            return SubscribeReqProto.SubscribeReq.getDefaultInstance().
                    getParserForType().parseFrom(array, offset, length);

        } else if (dataType == 0x01) {
            return SubscribeRespProto.SubscribeResp.getDefaultInstance().
                    getParserForType().parseFrom(array, offset, length);
        }

        return null; // or throw exception
    }
}
