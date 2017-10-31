package com.wh.test.netty.marshalling;

import com.wh.test.netty.protobuf.SubscribeReqProto;
import com.wh.test.netty.protobuf.SubscribeRespProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by wanghui on 17-10-30.
 */
public class SubReqServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        SuscribeReq req = (SuscribeReq)msg;

        System.out.format("Server accept client subscribe req: [%s]\n", req.toString());
        ctx.writeAndFlush(resp(req.getSubReqId()));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    private SuscribeResp resp(int subReqId){
        SuscribeResp resp = new SuscribeResp();

        resp.setSubReqId(subReqId);
        resp.setRespCode(0);
        resp.setDesc("Netty book order success, 3 days later, sent to the designate address");

        return resp;
    }

}
