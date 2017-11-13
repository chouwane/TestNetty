package com.wh.test.netty.com.wh.test.netty.afis;

import com.afis.net.DataGroup;
import com.afis.net.Message;
import com.wh.test.netty.afis.codec.AfisMessagePacket;
import io.netty.buffer.ByteBuf;

/**
 * Created by wanghui on 17-11-11.
 */
public class AfisMessagePacketTest {
    private static AfisMessagePacket packet = new AfisMessagePacket();

    public static void main(String[] args) {

        Message msg = AfisMessagePacket.createRequestMessage("12", System.currentTimeMillis(), "001");
        msg.newDataRow()
                 .setString("1","Hello")
                .setString("2","World")
        ;

        DataGroup group = new DataGroup();
        group.newDataRow()
                .setString("1","张")
                .setString("2","三")
                .setString("3", "丰")
        ;

        msg.get(0).setGroup("3", group);

        msg.newDataRow()
                .setString("1","Hello2")
                .setString("2","World2")
        ;

        ByteBuf b = packet.packet(msg);
        System.out.println("编码前："+msg);
        System.out.println("编码："+b);

        Message message = packet.unPack(b);
        System.out.println("解码："+message);

    }
}
