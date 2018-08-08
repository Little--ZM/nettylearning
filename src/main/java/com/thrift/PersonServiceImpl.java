package com.thrift;

import org.apache.thrift.TException;
import thrift.generated.DataException;
import thrift.generated.Person;
import thrift.generated.PersonService;


/**
 * 去实现一个服务端
 */
public class PersonServiceImpl implements PersonService.Iface {

    @Override
    public Person getPersonByUserName(String username) throws DataException, TException {

        System.out.println("get client request. get person by name");

        Person person = new Person();
        person.setMessage(username);
        person.setAge(Short.valueOf("20"));
        person.setMarriage(false);

        return person;
    }

    @Override
    public void savePerson(Person person) throws DataException, TException {

        System.out.println("--------------------------------------------");
        System.out.println("get a new person");

        System.out.println(person.getMessage());
        System.out.println(person.getAge());
        System.out.println(person.isMarriage());

    }

}
