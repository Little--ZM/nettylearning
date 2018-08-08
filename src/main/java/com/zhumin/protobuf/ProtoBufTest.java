package com.zhumin.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Created by charleszhu on 2017/6/9.
 */
public class ProtoBufTest {

    public static void main(String[] args) throws InvalidProtocolBufferException {

        DataInfo.Student student = DataInfo.Student.newBuilder()
                .setName("zhumin").setAge(18).setAddress("上海").build();

        byte[] student2ByteArray = student.toByteArray();

        DataInfo.Student  student2 = DataInfo.Student.parseFrom(student2ByteArray);

        System.out.println(student2.getName());
        System.out.println(student2.getAge());
        System.out.println(student2.getAddress());


    }

}
