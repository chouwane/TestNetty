package com.wh.test.netty.customer.handler;

import com.wh.test.netty.customer.msg.Header;
import com.wh.test.netty.customer.msg.MessageType;
import com.wh.test.netty.customer.msg.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author wanghui
 * @date 17-11-9
 */
public class HeadCheckReqHandler extends ChannelInboundHandlerAdapter {
    private volatile ScheduledFuture<?> heartBeat;


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage message = (NettyMessage) msg;

        //握手成功，主动发起心跳协议
        if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_RESP){

           heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx),0, 5, TimeUnit.SECONDS);

        }else  if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_RESP){
            System.out.println("The Client recieve heart message is : "+message);
        }
        else{
            ctx.fireChannelRead(message);
        }


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        if(heartBeat != null){
            heartBeat.cancel(true);
            heartBeat = null;
        }

        cause.printStackTrace();
        ctx.fireExceptionCaught(cause);
    }


    private class HeartBeatTask implements Runnable{
        private ChannelHandlerContext ctx;

        public HeartBeatTask(ChannelHandlerContext ctx){
            this.ctx = ctx;
        }

        @Override
        public void run() {

            NettyMessage heart = buildHeartBeat();
            System.out.println("Client send heart message to server : "+heart);
            ctx.writeAndFlush(heart);

        }

        private NettyMessage buildHeartBeat() {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.HEARTBEAT_REQ);
            message.setHeader(header);
            return message;
        }
    }



}
