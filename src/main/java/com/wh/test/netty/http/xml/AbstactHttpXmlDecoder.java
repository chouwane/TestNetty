package com.wh.test.netty.http.xml;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by wanghui on 17-11-7.
 */
public abstract class AbstactHttpXmlDecoder<T> extends MessageToMessageDecoder<T> {

    private IBindingFactory factory;
    private StringReader reader;
    private Class<?> clazz;
    private boolean isPrint;
    final static String CHARSET_NME = "UTF-8";
    final static Charset UTF_8 = Charset.forName(CHARSET_NME);

    public AbstactHttpXmlDecoder(Class<?> clazz){
        this(clazz, false);
    }

    public AbstactHttpXmlDecoder(Class<?> clazz, boolean isPrint){
        this.clazz = clazz;
        this.isPrint = isPrint;
    }

    protected Object decode0(ChannelHandlerContext ctx, ByteBuf body)throws Exception{
        factory = BindingDirectory.getFactory(clazz);
        String content = body.toString(UTF_8);
        if(isPrint){
            System.out.println("The body is : "+content);
        }

        reader = new StringReader(content);
        IUnmarshallingContext umctx = factory.createUnmarshallingContext();
        Object result = umctx.unmarshalDocument(reader);
        reader.close();
        reader = null;
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        //释放资源
        if(reader != null){
            reader.close();
            reader = null;
        }

    }

}
