package com.wh.test.netty.http.xml.response;

import com.wh.test.netty.http.xml.AbstactHttpXmlEncoder;
import com.wh.test.netty.http.xml.request.HttpXmlRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.net.InetAddress;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 *
 * @author wanghui
 * @date 17-11-2
 */
public class HttpXmlResponseEncoder extends AbstactHttpXmlEncoder<HttpXmlResponse> {
    @Override
    protected void encode(ChannelHandlerContext ctx, HttpXmlResponse msg, List<Object> out) throws Exception {

        ByteBuf body = encoder0(ctx,msg.getResult());
        FullHttpResponse response = msg.getResponse();

        if(response == null){
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK,body);
        }else {
            response = new DefaultFullHttpResponse(response.getProtocolVersion(),
                    response.getStatus(),
                    body);
        }

        response.headers().set(CONTENT_TYPE, "text/xml");
        HttpHeaders.setContentLength(response, body.readableBytes());
        out.add(response);
    }
}
