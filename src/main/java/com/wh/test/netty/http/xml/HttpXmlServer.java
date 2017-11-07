package com.wh.test.netty.http.xml;

import com.wh.test.netty.http.xml.pojo.Order;
import com.wh.test.netty.http.xml.request.HttpXmlRequestDecoder;
import com.wh.test.netty.http.xml.response.HttpXmlResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 *
 * @author wanghui
 * @date 17-10-28
 */
public class HttpXmlServer {

    public void bind(int port) throws Exception{

        //配置服务端的nio线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup wordGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, wordGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast("http decoder", new HttpRequestDecoder());
                            ch.pipeline().addLast("http aggregator", new HttpObjectAggregator(65536));
                            //XML解码器
                            ch.pipeline().addLast("xml decoder", new HttpXmlRequestDecoder(Order.class, true));

                            ch.pipeline().addLast("http encoder", new HttpResponseEncoder());
                            ch.pipeline().addLast("xml encoder", new HttpXmlResponseEncoder());

                            ch.pipeline().addLast(new HttpXmlServerHandler());

                        }
                    });

            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            System.out.println("HTTP 订购服务启动, 地址是： " + "http://localhost:" + port );

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

        new HttpXmlServer().bind(port);

    }

}
