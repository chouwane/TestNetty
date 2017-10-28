package com.wh.test.netty.delimiter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by wanghui on 17-10-28.
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    int counter = 0;
    static final String ECHO_REQ = "Hi, Welcome to Netty. $_";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //连接成功后就发送10条消息
        for(int i=0; i<10; i++){
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String body = (String)msg;

        System.out.format("This is %d times recieve server : [%s]\n", ++counter, body);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
