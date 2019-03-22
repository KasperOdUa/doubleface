package com.example.doubleface.security;


import com.example.doubleface.model.Person;
import org.springframework.security.core.authority.AuthorityUtils;

public class SpringPerson extends org.springframework.security.core.userdetails.User {

    private Person person;

    public SpringPerson(Person person) {
        super(person.getEmail(), person.getPassword(), AuthorityUtils.createAuthorityList("person"));
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}