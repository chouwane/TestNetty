package com.wh.test.netty.protobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghui on 17-10-30.
 */
public class SubReqClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        for(int i=0; i<10 ; i++){
            ctx.write(subreq(i));
        }

        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.format("Recieve server response : [%s]\n", msg);

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



    private SubscribeReqProto.SubscribeReq subreq(int i){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();

        builder.setSubReqId(i);
        builder.setUserName("zhangsan");
        builder.setProductName("Netty book for protobuf");
        List<String> address = new ArrayList<>();
        address.add("Nanjing");
        address.add("Beijing");
        address.add("Tianjing");
        builder.addAllAddress(address);

        return builder.build();
    }
}
