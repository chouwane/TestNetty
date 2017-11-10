package com.wh.test.netty.customer;

import com.wh.test.netty.customer.codec.NettyMessageDecoder;
import com.wh.test.netty.customer.codec.NettyMessageEncoder;
import com.wh.test.netty.customer.handler.HeadCheckReqHandler;
import com.wh.test.netty.customer.handler.LoginAuthReqHandler;
import com.wh.test.netty.delimiter.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanghui on 17-10-28.
 */
public class NettyClient {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private EventLoopGroup group = new NioEventLoopGroup();


    public void connect(String host, int port) throws Exception{

        try {

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast("NettyMessageDecoder",new NettyMessageDecoder(1024*1024, 4,4));
                            ch.pipeline().addLast("NettyMessageEncoder",new NettyMessageEncoder());
                            ch.pipeline().addLast("ReadTimeoutHandler",new ReadTimeoutHandler(500));
                            ch.pipeline().addLast("LoginAuthReqHandler",new LoginAuthReqHandler());
                            ch.pipeline().addLast("HeadCheckReqHandler",new HeadCheckReqHandler());

                        }
                    });

            //发起异步连接操作
            ChannelFuture f  = b.connect(host, port).sync();

            System.out.println(new Date()+"连接到服务器");

            //等待客户端链路关闭
            f.channel().closeFuture().sync();
        }finally {
            //所有资源释放完毕后，清空资源，再次发起重连请求
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        connect("127.0.0.1", port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
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

        new NettyClient().connect("127.0.0.1", port);

    }

}
