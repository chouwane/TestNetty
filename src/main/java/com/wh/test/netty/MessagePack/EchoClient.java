package com.wh.test.netty.MessagePack;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Created by wanghui on 17-10-28.
 */
public class EchoClient {

    public void connect(String host, int port) throws Exception{

        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast("frame decoder", new LengthFieldBasedFrameDecoder(65535,0,2,0,2));
                            ch.pipeline().addLast("msgpack decoder", new MsgpackDecoder());

                            //在解码前添加两个字节的消息长度字段
                            ch.pipeline().addLast("frame encoder", new LengthFieldPrepender(2));
                            ch.pipeline().addLast("msgpack encoder", new MsgpackEncoder());

                            ch.pipeline().addLast(new EchoClientHandler(10));

                        }
                    });

            //发起异步连接操作
            ChannelFuture f  = b.connect(host, port).sync();

            //等待客户端链路关闭
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }

    }


    public static void main(String[] args) throws Exception {

        int port = 8081;

        if(args != null && args.length > 0){
            try {
                port = Integer.parseInt(args[0]);
            }catch (NumberFormatException e){
                //采用默认值
            }
        }

        new EchoClient().connect("127.0.0.1", port);

    }

}
