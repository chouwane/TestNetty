package com.wh.test.netty.marshalling;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author wanghui
 * @date 17-10-30
 */
public class SuscribeReq implements Serializable {
    private static final long serialVersionUID = 3870422348388353787L;

    private int subReqId;
    private String userName;
    private String productName;
    private List<String> address;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getSubReqId() {
        return subReqId;
    }

    public void setSubReqId(int subReqId) {
        this.subReqId = subReqId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }


    @Override
    public String toString() {
        return "SuscribeReq{" +
                "subReqId=" + subReqId +
                ", userName='" + userName + '\'' +
                ", productName='" + productName + '\'' +
                ", address=" + address +
                '}';
    }
}
