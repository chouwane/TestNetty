package com.wh.test.netty.http.xml;

import com.wh.test.netty.delimiter.EchoClientHandler;
import com.wh.test.netty.http.xml.pojo.Order;
import com.wh.test.netty.http.xml.request.HttpXmlRequestEncoder;
import com.wh.test.netty.http.xml.response.HttpXmlResponseDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

/**
 *
 * @author wanghui
 * @date 17-10-28
 */
public class HttpXmlClient {

    public void connect(String host, int port) throws Exception{

        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast("http decoder", new HttpResponseDecoder());
                            ch.pipeline().addLast("http aggregator", new HttpObjectAggregator(65536));
                            //XML解码器
                            ch.pipeline().addLast("xml decoder", new HttpXmlResponseDecoder(Order.class, true));

                            ch.pipeline().addLast("http encoder", new HttpRequestEncoder());
                            ch.pipeline().addLast("xml encoder", new HttpXmlRequestEncoder());

                            ch.pipeline().addLast(new HttpXmlClientHandler());

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

        new HttpXmlClient().connect("127.0.0.1", port);

    }

}
