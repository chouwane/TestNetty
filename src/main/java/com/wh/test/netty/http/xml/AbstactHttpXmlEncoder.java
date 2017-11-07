package com.wh.test.netty.http.xml;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * Created by wanghui on 17-11-7.
 */
public abstract class AbstactHttpXmlEncoder<T> extends MessageToMessageEncoder<T> {
    IBindingFactory factory;
    StringWriter writer;
    final static String CHARSET_NME = "UTF-8";
    final static Charset UTF_8 = Charset.forName(CHARSET_NME);

    protected ByteBuf encoder0(ChannelHandlerContext ctx, Object body) throws Exception{

        factory = BindingDirectory.getFactory(body.getClass());
        writer = new StringWriter();
        IMarshallingContext mctx = factory.createMarshallingContext();
        mctx.setIndent(2);
        mctx.marshalDocument(body,CHARSET_NME,null,writer);
        String xmlstr = writer.toString();
        ByteBuf encodeBuf = Unpooled.copiedBuffer(xmlstr, UTF_8);
        writer.close();
        writer = null;
        return encodeBuf;

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        //释放资源
        if(writer != null){
            writer.close();
            writer = null;
        }

    }
}
