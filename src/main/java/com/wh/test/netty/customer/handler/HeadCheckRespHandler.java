package com.wh.test.netty.customer.handler;

import com.wh.test.netty.customer.msg.Header;
import com.wh.test.netty.customer.msg.MessageType;
import com.wh.test.netty.customer.msg.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *
 * @author wanghui
 * @date 17-11-9
 */
public class HeadCheckRespHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage message = (NettyMessage) msg;

        //握手成功，主动发起心跳协议
        if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_REQ){
            System.out.println("recieve client heart message is : "+message);
            NettyMessage res = buildHeartBeat();
            ctx.writeAndFlush(res);
        }
        else{
            ctx.fireChannelRead(message);
        }


    }

    private NettyMessage buildHeartBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP);
        message.setHeader(header);
        return message;
    }


}
