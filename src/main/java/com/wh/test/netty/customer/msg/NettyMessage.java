package com.wh.test.netty.customer.msg;

import java.io.Serializable;

/**
 * Created by wanghui on 17-11-9.
 */
public final class NettyMessage implements Serializable {
    private Header header;
    private Object body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NettyMessage{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
