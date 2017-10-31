package com.wh.test.netty.marshalling;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * marshalling的编码解码器工厂类
 * Created by wanghui on 17-10-30.
 */
public class MarshallingCodeFactory {

    public static MarshallingDecoder buildMarshallingDecoder(){
        // "serial"表示java序列化工厂对象
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);

        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);

        MarshallingDecoder decoder = new MarshallingDecoder(provider);

        return decoder;
    }

    public static MarshallingEncoder buildMarshallingEncoder(){

        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);

        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);

        MarshallingEncoder encoder = new MarshallingEncoder(provider);

        return encoder;
    }

}
