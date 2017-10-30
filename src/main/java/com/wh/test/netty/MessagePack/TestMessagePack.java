package com.wh.test.netty.MessagePack;

import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * msgPack的用法
 * Created by wanghui on 17-10-28.
 */
public class TestMessagePack {

    public static void main(String[] args) throws IOException {

        MessagePack msgpack = new MessagePack();

        User u = new User("张三", 30);

        byte[] b = msgpack.write(u);
        System.out.println(msgpack.read(b));

    }

}
