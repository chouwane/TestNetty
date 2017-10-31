package com.wh.test.netty.marshalling;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghui on 17-10-30.
 */
public class MarshallingTest {
    private static  final MarshallerFactory marshallerFactory;
    private static final MarshallingConfiguration configuration;

    static {
        marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
    }

    private static SuscribeReq buildSuscribeReq(){
        SuscribeReq req = new SuscribeReq();
        req.setSubReqId(1);
        req.setUserName("张三");
        req.setProductName("Netty book");
        List<String> address = new ArrayList<>();
        address.add("Nanjing");
        address.add("Beijing");
        address.add("Tianjing");
        req.setAddress(address);
        return req;
    }

    private static byte[] encode(SuscribeReq req) throws IOException {
        Marshaller marshaller = marshallerFactory.createMarshaller(configuration);

        //marshaller.start(bo);

        return null;
    }


    public static void main(String[] args) {

        SuscribeReq req = buildSuscribeReq();


    }

}
