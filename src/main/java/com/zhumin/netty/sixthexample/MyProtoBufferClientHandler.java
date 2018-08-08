package com.zhumin.netty.sixthexample;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;

/**
 * Created by charleszhu on 2017/6/9.
 */
public class MyProtoBufferClientHandler extends SimpleChannelInboundHandler<MyMessage.ShengWu> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyMessage.ShengWu msg) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        int dataType = new Random().nextInt(3);

        MyMessage.ShengWu shengWu = null;

        if(0 == dataType) {
            shengWu = MyMessage.ShengWu.newBuilder().setDataType(MyMessage.ShengWu.DataType.PersonType).setPerson(
                    MyMessage.Person.newBuilder().setName("敏哥").setAge(18).setAddress("上海").build()
            ).build();
        } else if(1 == dataType) {
            shengWu = MyMessage.ShengWu.newBuilder().setDataType(MyMessage.ShengWu.DataType.DogType).setDog(
                    MyMessage.Dog.newBuilder().setName("一只狗").setCity("上海").build()
            ).build();
        } else {
            shengWu = MyMessage.ShengWu.newBuilder().setDataType(MyMessage.ShengWu.DataType.CatType).setCat(
                    MyMessage.Cat.newBuilder().setName("一只猫").setAge(23).build()
            ).build();
        }

        ctx.writeAndFlush(shengWu);

//        MyDataInfo.MyPerson myPerson = MyDataInfo.MyPerson.newBuilder().setName("敏哥").setAge(18).setAddress("上海").build();
//        ctx.writeAndFlush(myPerson);
    }
}
