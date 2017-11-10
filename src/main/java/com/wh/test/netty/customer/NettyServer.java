package com.wh.test.netty.customer;

import com.wh.test.netty.customer.codec.NettyMessageDecoder;
import com.wh.test.netty.customer.codec.NettyMessageEncoder;
import com.wh.test.netty.customer.handler.HeadCheckReqHandler;
import com.wh.test.netty.customer.handler.HeadCheckRespHandler;
import com.wh.test.netty.customer.handler.LoginAuthReqHandler;
import com.wh.test.netty.customer.handler.LoginAuthRespHandler;
import com.wh.test.netty.delimiter.EchoServerHandler;
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
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4JLoggerFactory;

/**
 * Created by wanghui on 17-10-28.
 */
public class NettyServer {

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

                            ch.pipeline().addLast(new NettyMessageDecoder(1024*1024, 4,4));
                            ch.pipeline().addLast(new NettyMessageEncoder());
                            ch.pipeline().addLast("ReadTimeoutHandler",new ReadTimeoutHandler(50));
                            ch.pipeline().addLast(new LoginAuthRespHandler());
                            ch.pipeline().addLast("HeadCheckResqHandler",new HeadCheckRespHandler());
                        }
                    });

            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            System.out.println("服务启动成功");

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

        new NettyServer().bind(port);

    }

}
