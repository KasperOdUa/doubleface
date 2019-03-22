package com.example.doubleface.security;

import com.example.doubleface.model.Person;
import com.example.doubleface.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PersonDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        Person byEmail = personRepository.findByEmail(s);
        if (byEmail == null) {
            throw new UsernameNotFoundException("Person with does not exists");
        }
        return new SpringPerson(byEmail);
    }
}