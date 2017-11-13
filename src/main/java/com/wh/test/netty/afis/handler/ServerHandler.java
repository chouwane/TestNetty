package com.wh.test.netty.afis.handler;

import com.afis.net.Message;
import com.wh.test.netty.afis.codec.AfisMessagePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by wanghui on 17-11-13.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof Message) {
            System.out.println("收到 : " + msg);
            Message request = (Message) msg;
            Message response = AfisMessagePacket.createResponseMessage(request, 0);
            response.add(request.get(0));
            ctx.writeAndFlush(response);
        }

    }
}
