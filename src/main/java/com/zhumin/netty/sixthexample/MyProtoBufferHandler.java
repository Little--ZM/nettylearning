package com.zhumin.netty.sixthexample;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by charleszhu on 2017/6/9.
 */
public class MyProtoBufferHandler extends SimpleChannelInboundHandler<MyMessage.ShengWu> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyMessage.ShengWu msg) throws Exception {

        MyMessage.ShengWu.DataType dataType = msg.getDataType();
        if(dataType == MyMessage.ShengWu.DataType.PersonType) {
            MyMessage.Person person = msg.getPerson();
            System.out.println(person.getName());
            System.out.println(person.getAge());
            System.out.println(person.getAddress());
        } else if(dataType == MyMessage.ShengWu.DataType.CatType) {
            MyMessage.Cat cat = msg.getCat();
            System.out.println(cat.getName());
            System.out.println(cat.getAge());
        } else {
            MyMessage.Dog dog = msg.getDog();
            System.out.println(dog.getName());
            System.out.println(dog.getCity());
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }
}
