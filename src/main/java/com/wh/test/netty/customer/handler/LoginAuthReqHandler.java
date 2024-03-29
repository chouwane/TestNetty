package com.wh.test.netty.customer.handler;

import com.wh.test.netty.customer.msg.Header;
import com.wh.test.netty.customer.msg.MessageType;
import com.wh.test.netty.customer.msg.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by wanghui on 17-11-9.
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginReq());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage message = (NettyMessage) msg;

        //如果是握手应答消息，需要判断是否认证成功
        if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_RESP){

            byte loginResult = (byte)message.getBody();
            if(loginResult != (byte)0){
                //握手失败，关闭连接
                ctx.close();
            }else{
                System.out.println("Login is ok : "+message);
                ctx.fireChannelRead(message);
            }

        }else{
            ctx.fireChannelRead(message);
        }


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    private Object buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ);
        message.setHeader(header);
        System.out.println("buildLoginReq: "+message);
        return message;
    }
}
