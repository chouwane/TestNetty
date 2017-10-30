package com.wh.test.netty.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by wanghui on 17-10-30.
 */
public class SubReqServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq)msg;

        System.out.format("Server accept client subscribe req: [%s]\n", req.toString());
        ctx.writeAndFlush(resp(req.getSubReqId()));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    private SubscribeRespProto.SubscribeResp resp(int subReqId){
        SubscribeRespProto.SubscribeResp.Builder build = SubscribeRespProto.SubscribeResp.newBuilder();

        build.setSubReqId(subReqId);
        build.setRespCode(0);
        build.setDesc("Netty book order success, 3 days later, sent to the designate address");

        return build.build();
    }

}
