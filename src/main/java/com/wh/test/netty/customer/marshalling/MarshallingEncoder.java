package com.wh.test.netty.customer.marshalling;


import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;


/**
 * Created by wanghui on 17-11-9.
 */
public class MarshallingEncoder {

    private final static byte[] LENTH_PLACEHOLDER = new byte[4];
    Marshaller marshaller;

    public MarshallingEncoder() throws IOException {
        marshaller = MarshallingCodecFactory.buildMarshaling();
    }

    public void encode(Object msg, ByteBuf out) throws Exception{

        try {
            int lengthPos = out.writerIndex();
            out.writeBytes(LENTH_PLACEHOLDER);
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
            marshaller.start(output);
            marshaller.writeObject(msg);
            marshaller.finish();
            out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
        }finally {
            marshaller.close();
        }

    }

}
