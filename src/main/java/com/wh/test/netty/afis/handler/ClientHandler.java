package com.wh.test.netty.afis.handler;

import com.afis.net.Message;
import com.wh.test.netty.afis.codec.AfisMessagePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by wanghui on 17-11-13.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i=1; i<= 100; i++) {
            Message request = AfisMessagePacket.createRequestMessage(i+"", System.currentTimeMillis(), "001");
            request.newDataRow()
                    .setString("1", "hello"+i)
                    .setString("2", "world"+i)
            ;
            ctx.write(request);
        }

        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof Message) {
            Message message = (Message) msg;
            System.out.println("收到 : " + message);
        }
    }
}
