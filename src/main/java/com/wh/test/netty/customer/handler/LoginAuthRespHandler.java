package com.wh.test.netty.customer.handler;

import com.wh.test.netty.customer.msg.Header;
import com.wh.test.netty.customer.msg.MessageType;
import com.wh.test.netty.customer.msg.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wanghui on 17-11-9.
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter{

    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>();

    private String[] whiteList = new String[]{"127.0.0.1"};

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        NettyMessage message = (NettyMessage) msg;

        //如果是握手请求消息处理，其他消息透传
        if(message.getHeader() != null
                && message.getHeader().getType() == MessageType.LOGIN_REQ){

            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            //重复登录，拒绝
            if(nodeCheck.containsKey(nodeIndex)){
                loginResp = buildResponse((byte)-1);
            }else {
                InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOk = false;

                for(String WIP : whiteList){
                    if(WIP.equals(ip)){
                        isOk = true;
                        break;
                    }
                }

                loginResp = isOk?buildResponse((byte)0) : buildResponse((byte)-1);
                if(isOk){
                    nodeCheck.put(nodeIndex, true);
                }

                System.out.println("The login resp is : "+loginResp);
                ctx.writeAndFlush(loginResp);
            }

        }else{
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        nodeCheck.remove(ctx.channel().remoteAddress().toString());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }

    private NettyMessage buildResponse(byte result) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP);
        message.setHeader(header);
        message.setBody(result);
        return message;
    }
}
