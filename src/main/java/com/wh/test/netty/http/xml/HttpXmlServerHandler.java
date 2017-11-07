package com.wh.test.netty.http.xml;

import com.wh.test.netty.http.xml.pojo.Address;
import com.wh.test.netty.http.xml.pojo.Customer;
import com.wh.test.netty.http.xml.pojo.Order;
import com.wh.test.netty.http.xml.pojo.Shipping;
import com.wh.test.netty.http.xml.request.HttpXmlRequest;
import com.wh.test.netty.http.xml.response.HttpXmlResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.ArrayList;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

/**
 * Created by wanghui on 17-11-7.
 */
public class HttpXmlServerHandler extends SimpleChannelInboundHandler<HttpXmlRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpXmlRequest msg) throws Exception {
        FullHttpRequest request = msg.getRequest();
        Order order = (Order) msg.getBody();
        System.out.println("Http server recevie request : "+order);

        ChannelFuture future = ctx.writeAndFlush(new HttpXmlResponse(null, order));
        future.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                ctx.close();
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if(ctx.channel().isActive()){
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
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
