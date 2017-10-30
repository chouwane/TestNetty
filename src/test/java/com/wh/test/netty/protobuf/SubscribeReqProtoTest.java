package com.wh.test.netty.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghui on 17-10-30.
 */
public class SubscribeReqProtoTest {

    private static byte[] encode(SubscribeReqProto.SubscribeReq req){

        return req.toByteArray();

    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {

        return SubscribeReqProto.SubscribeReq.parseFrom(body);

    }


    private static SubscribeReqProto.SubscribeReq createSubscribeReq(){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();

        builder.setSubReqId(1);
        builder.setUserName("张三");
        builder.setProductName("Netty book");
        List<String> address = new ArrayList<>();
        address.add("Nanjing");
        address.add("Beijing");
        address.add("Tianjing");
        builder.addAllAddress(address);

        return builder.build();

    }


    public static void main(String[] args) throws InvalidProtocolBufferException {
        SubscribeReqProto.SubscribeReq req = createSubscribeReq();
        System.out.println("编码前 : "+req.toString());

        byte[] body = encode(req);
        SubscribeReqProto.SubscribeReq req2 = decode(body);
        System.out.println("解码后 : "+req2.toString());

        System.out.println("编码前后是否相等 ： "+ req2.equals(req));

    }

}
