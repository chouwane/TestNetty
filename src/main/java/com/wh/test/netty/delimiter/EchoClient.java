package com.wh.test.netty.delimiter;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

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
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new EchoClientHandler());

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
