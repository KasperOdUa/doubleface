package com.example.doubleface.service;

import com.example.doubleface.model.Person;
import com.example.doubleface.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.UUID;
@Service
public class PersonService implements UserDetailsService{

    @Autowired
    private EmailService emailService;

    @Autowired
    private PersonRepository personRepository;

    public boolean activate(Person person) {
        person.setActivationCode(UUID.randomUUID().toString());
        personRepository.save(person);
        if (!StringUtils.isEmpty(person.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Sweater. Please, visit next link: http://localhost:8080/activate/%s",
                    person.getName(),
                    person.getActivationCode()
            );

            emailService.sendSimpleMessage(person.getEmail(), "Activation code", message);
        }

        return true;
    }

    public boolean activatePerson(String code) {
        Person person = personRepository.findByActivationCode(code);
        if (person == null) {
            return false;
        }
        person.setActivationCode(null);
        personRepository.save(person);

        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        return (UserDetails) personRepository.findByName(name);
    }
}
