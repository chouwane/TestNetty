package com.wh.test.netty.MessagePack;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by wanghui on 17-10-28.
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    private int sendNumber;

    public EchoClientHandler(int sendNumber){
        this.sendNumber = sendNumber;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        User[] users = getUsers();

        for(User u : users){
            ctx.write(u);
        }

        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.format("Client recieve the msgpack message : %s\n", msg);
        //ctx.write(msg);

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


    private User[] getUsers(){

        User[] users = new User[sendNumber];

        User u = null;
        for(int i=1; i<= sendNumber; i++){
            u = new User("name"+i, i);
            users[i-1] = u;
        }

        return users;
    }
}
