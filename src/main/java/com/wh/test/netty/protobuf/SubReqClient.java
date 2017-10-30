package com.wh.test.netty.protobuf;

import com.wh.test.netty.MessagePack.EchoClientHandler;
import com.wh.test.netty.MessagePack.MsgpackDecoder;
import com.wh.test.netty.MessagePack.MsgpackEncoder;
import com.wh.test.netty.protobuf.customer.CustomProtobufDecoder;
import com.wh.test.netty.protobuf.customer.CustomProtobufEncoder;
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
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * Created by wanghui on 17-10-28.
 */
public class SubReqClient {

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

                            /*//处理半包
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            //解码
                            ch.pipeline().addLast(new ProtobufDecoder(SubscribeRespProto.SubscribeResp.getDefaultInstance()));

                            //
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            //编码
                            ch.pipeline().addLast(new ProtobufEncoder());*/

                            ch.pipeline().addLast(new CustomProtobufDecoder());
                            ch.pipeline().addLast(new CustomProtobufEncoder());

                            ch.pipeline().addLast(new SubReqClientHandler());

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

        new SubReqClient().connect("127.0.0.1", port);

    }

}
