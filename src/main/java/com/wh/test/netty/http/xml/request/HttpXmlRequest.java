package com.wh.test.netty.http.xml.request;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Created by wanghui on 17-11-7.
 */
public class HttpXmlRequest {
    private FullHttpRequest request;
    private Object body;

    public HttpXmlRequest(FullHttpRequest request, Object body) {
        this.request = request;
        this.body = body;
    }

    public final FullHttpRequest getRequest() {
        return request;
    }

    public final void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public final Object getBody() {
        return body;
    }

    public final void setBody(Object body) {
        this.body = body;
    }
}
