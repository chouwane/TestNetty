package com.wh.test.netty.http.file;

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
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 *
 * @author wanghui
 * @date 17-10-28
 */
public class HttpFileServer {

    private static final String DEFAULT_URL = "/TestNetty/";

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

                            //http请求解码器
                            ch.pipeline().addLast("http decoder", new HttpRequestDecoder());

                            /*
                                HttpObjectAggregator解码器，它的作用是将多个消息转换为单一的FullHttpRequest或FullHttpResponse
                                原因是http解码器在每一个http消息中会生成多个消息对象：HttpRequest、Http[Response、HttpContent、LastHttpContent
                             */
                            ch.pipeline().addLast("http aggregator", new HttpObjectAggregator(65536));

                            //http响应编码器
                            ch.pipeline().addLast("http encoder", new HttpResponseEncoder());

                            /*
                            ChunkedWriteHandler 主要作用是支持异步发送大的码流（例如大的文件传输）,
                            但不占用过多的内存，防止发生java内存溢出错误
                             */
                            ch.pipeline().addLast("http chunked", new ChunkedWriteHandler());
                            ch.pipeline().addLast("fileserverHandler",new HttpFileServerHandler(DEFAULT_URL));
                        }
                    });

            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            System.out.println("HTTP 文件服务器启动, 地址是： " + "http://localhost:" + port + DEFAULT_URL);

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

        new HttpFileServer().bind(port);

    }

}
