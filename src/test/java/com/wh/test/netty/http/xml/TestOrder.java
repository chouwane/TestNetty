package com.wh.test.netty.http.xml;

import com.wh.test.netty.http.xml.pojo.Address;
import com.wh.test.netty.http.xml.pojo.Customer;
import com.wh.test.netty.http.xml.pojo.Order;
import com.wh.test.netty.http.xml.pojo.Shipping;
import org.jibx.runtime.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghui on 17-11-2.
 */
public class TestOrder {

    private static final String CHARSET_NAME = "UTF-8";
    private IBindingFactory factory = null;
    private StringWriter writer = null;
    private StringReader reader = null;

    private String encode2Xml(Order order) throws JiBXException, IOException {

        factory = BindingDirectory.getFactory(Order.class);
        writer = new StringWriter();
        IMarshallingContext mctx  = factory.createMarshallingContext();
        mctx.setIndent(2);//设置缩进
        mctx.marshalDocument(order, CHARSET_NAME, null, writer);

        String xmlStr = writer.toString();
        writer.close();
        System.out.println(xmlStr);
        return xmlStr;
    }

    private Order decoder2Order(String xmlBody) throws JiBXException {

        reader = new StringReader(xmlBody);
        IUnmarshallingContext uctx = factory.createUnmarshallingContext();
        Order order = (Order)uctx.unmarshalDocument(reader);
        return order;
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

    public static void main(String[] args) throws JiBXException, IOException {
        TestOrder t = new TestOrder();

        Order o = t.builOrder();
        String xmlStr = t.encode2Xml(o);

        System.out.println(t.decoder2Order(xmlStr));

        /*
            注意，有时候会出现异常信息，如：java.lang.NoSuchFieldException: JiBX_bindingXXXX
            就要重复下面的命令就可以了。
            java -cp bin;lib/jibx-bind.jar org.jibx.binding.Compile -v bind.xml
            或者使用maven插件编译几下就行了

            可以参考：http://blog.csdn.net/majian_1987/article/details/43702817
         */

    }


}
