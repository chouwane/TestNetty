package com.wh.test.netty.delimiter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.ByteBuffer;

/**
 * Created by wanghui on 17-10-28.
 */
public class EchoServer {

    public void bind(int port) throws Exception{

        //配置服务端的nio线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup wordGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, wordGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1000)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            //以"$_"作为分隔符
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });

            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        }finally {
            //优雅的退出，释放线程资源
            bossGroup.shutdownGracefully();
            wordGroup.shutdownGracefully();
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

        new EchoServer().bind(port);

    }

}
