package com.wh.test.netty.http.xml;

/**
 * Created by wanghui on 17-11-1.
 */
public class Birthday {

    private String birthday;

    public Birthday(String birthday) {
        super();
        this.birthday = birthday;
    }
    public Birthday() {}

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Birthday{" +
                "birthday='" + birthday + '\'' +
                '}';
    }
}
