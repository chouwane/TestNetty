package com.wh.test.netty.MessagePack;

import org.msgpack.annotation.Message;

import java.io.Serializable;

/**
 * Created by wanghui on 17-10-28.
 */
@Message
public class User implements Serializable {

    private static final long serialVersionUID = 9135155430805683220L;
    private String name;
    private int age;

    public User(){}

    public User(String name, int age){
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
