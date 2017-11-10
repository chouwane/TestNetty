package com.wh.test.netty.customer.marshalling;


import org.jboss.marshalling.*;

import java.io.IOException;

/**
 * Created by wanghui on 17-11-9.
 */
public class MarshallingCodecFactory {
    private static  final MarshallerFactory marshallerFactory;
    private static final MarshallingConfiguration configuration;

    static {
        marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
    }

    public static Marshaller buildMarshaling() throws IOException {
        return marshallerFactory.createMarshaller(configuration);
    }

    public static Unmarshaller buildUnMarshaling() throws IOException {
        return marshallerFactory.createUnmarshaller(configuration);
    }
}
