package com.zyx.thrift;

import org.apache.thrift.TException;

public class PersonServiceImpl implements PersonService.Iface {
    @Override
    public Person getPersonByUsername(String username) throws DataException, TException {
        System.out.println("Got Client Param : " + username);

        Person person = new Person();
        person.setAge(20);
        person.setMarried(false);
        person.setUsername("张三");

        return person;
    }

    @Override
    public void savePerson(Person person) throws DataException, TException {
        System.out.println("Got Client Param : ");
        System.out.println(person.getAge());
        System.out.println(person.getUsername());
        System.out.println(person.isMarried());
    }
}
