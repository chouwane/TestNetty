package com.wh.test.netty.websocket;

import com.sun.xml.internal.txw2.TXW;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

/**
 * Created by wanghui on 17-11-8.
 */
public class WebsocketServerHandler extends SimpleChannelInboundHandler<Object>{


    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        //传统的Http接入
        if(msg instanceof FullHttpRequest){
            handlerHttpRequest(ctx, (FullHttpRequest)msg);
        }
        //websocket接入
        else if(msg instanceof WebSocketFrame){
            handlerWebsocket(ctx, (WebSocketFrame)msg);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {

        //如果Http解码失败，则返回http异常
        if(!req.getDecoderResult().isSuccess()
                || !"websocket".equals(req.headers().get("Upgrade"))){

            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;

        }

        //构造握手响应返回
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(
                "ws://localhost:8081/websocket",null,false
        );

        handshaker = factory.newHandshaker(req);
        if(handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else {
            handshaker.handshake(ctx.channel(), req);
        }

    }

    private void handlerWebsocket(ChannelHandlerContext ctx, WebSocketFrame frame) {

        //判断是否是关闭链路的命令
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(), (CloseWebSocketFrame)frame.retain());
            return;
        }

        //判断是否是Ping消息
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        //本例支持文本消息，不支持二进制消息
        if(!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format("%s frame types not supported",frame.getClass().getName()));
        }

        //应答消息
        String request = ((TextWebSocketFrame) frame).text();
        System.out.format("%s receive %s\n",ctx.channel(), request);

        ctx.write(new TextWebSocketFrame(request+", 欢迎使用Netty Websocket服务，现在时刻："+new Date().toString()));
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res){

        //返回应答给客户端
        if(res.getStatus().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(res, res.content().readableBytes());
        }

        //如果非Keep-Alive，则关闭连接
        System.out.println("Connection = "+req.headers().get(HttpHeaders.Names.CONNECTION));
        ChannelFuture f = ctx.writeAndFlush(res);
        if(!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200){
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
