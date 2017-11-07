package com.wh.test.netty.http.xml.response;

import com.wh.test.netty.http.xml.AbstactHttpXmlDecoder;
import com.wh.test.netty.http.xml.request.HttpXmlRequest;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

/**
 * Created by wanghui on 17-11-7.
 */
public class HttpXmlResponseDecoder extends AbstactHttpXmlDecoder<FullHttpResponse> {
    public HttpXmlResponseDecoder(Class<?> clazz) {
        super(clazz);
    }

    public HttpXmlResponseDecoder(Class<?> clazz, boolean isPrint) {
        super(clazz, isPrint);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpResponse msg, List<Object> out) throws Exception {

        if(!msg.getDecoderResult().isSuccess()){
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        HttpXmlResponse response = new HttpXmlResponse(msg, decode0(ctx, msg.content()));
        out.add(response);

    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
