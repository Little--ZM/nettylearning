package com.thrift;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.*;
import thrift.generated.Person;
import thrift.generated.PersonService;

public class ThriftClient {

    public static void main(String[] args) {

        TTransport tTransport = new TFramedTransport(new TSocket("localhost", 8899),600);
        TProtocol protocol = new TCompactProtocol(tTransport);
        PersonService.Client client = new PersonService.Client(protocol);

        try {
            long start = System.currentTimeMillis();
            tTransport.open();
            System.out.println(System.currentTimeMillis() - start);


            Person person = client.getPersonByUserName("张三");

            System.out.println(person.getMessage());
            System.out.println(person.getAge());
            System.out.println(person.isMarriage());

        } catch (Exception e) {



        } finally {
            tTransport.close();
        }

    }

}
