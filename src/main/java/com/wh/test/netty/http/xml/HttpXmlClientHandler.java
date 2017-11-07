package com.wh.test.netty.http.xml;

import com.wh.test.netty.http.xml.pojo.Address;
import com.wh.test.netty.http.xml.pojo.Customer;
import com.wh.test.netty.http.xml.pojo.Order;
import com.wh.test.netty.http.xml.pojo.Shipping;
import com.wh.test.netty.http.xml.request.HttpXmlRequest;
import com.wh.test.netty.http.xml.response.HttpXmlResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghui on 17-11-7.
 */
public class HttpXmlClientHandler extends SimpleChannelInboundHandler<HttpXmlResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        HttpXmlRequest request = new HttpXmlRequest(null, builOrder());
        ctx.writeAndFlush(request);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpXmlResponse msg) throws Exception {
        System.out.println("The clisent recieve response of http header is "+msg.getResponse().headers().names());
        System.out.println("The clisent recieve response of http body is "+msg.getResult());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private Order builOrder(){

        Customer customer = new Customer();
        customer.setCustomerNumber(123);
        customer.setFirstName("张");
        customer.setLastName("三");
        List<String> middleName = new ArrayList<>();
        middleName.add("一");
        middleName.add("二");
        customer.setMiddleName(middleName);

        Address billTo = new Address();
        billTo.setCountry("中国");
        billTo.setState("江苏省");
        billTo.setCity("南京市");
        billTo.setStreet1("龙眠大道");
        billTo.setPostCode("123321");

        Address shipTo = new Address();
        shipTo.setCountry("中国");
        shipTo.setState("江苏省");
        shipTo.setCity("苏州市");
        shipTo.setStreet1("龙眠大道");
        shipTo.setPostCode("123321");

        Order order = new Order();
        order.setOrderNumber(123);
        order.setTotal(9999.999f);
        order.setCustomer(customer);
        order.setBillTo(billTo);
        order.setShipping(Shipping.INTERNATIONAL_MAIL);
        order.setShipTo(shipTo);
        return order;
    }
}
