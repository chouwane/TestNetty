package com.wh.test.netty.marshalling;

import java.io.Serializable;

/**
 * Created by wanghui on 17-10-30.
 */
public class SuscribeResp implements Serializable{

    private static final long serialVersionUID = 6802439218806484194L;

    private  int subReqId;
    private int respCode;
    private String desc;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getSubReqId() {
        return subReqId;
    }

    public void setSubReqId(int subReqId) {
        this.subReqId = subReqId;
    }

    public int getRespCode() {
        return respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "SuscribeResp{" +
                "subReqId=" + subReqId +
                ", respCode=" + respCode +
                ", desc='" + desc + '\'' +
                '}';
    }
}
