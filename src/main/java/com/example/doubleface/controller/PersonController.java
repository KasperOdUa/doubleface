package com.example.doubleface.controller;


import com.example.doubleface.model.Person;
import com.example.doubleface.repository.PersonRepository;
import com.example.doubleface.security.SpringPerson;
import com.example.doubleface.service.EmailService;
import com.example.doubleface.service.PersonService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Controller
public class PersonController {

    @Value("${image.upload.dir}")
    private String imageUploadDir;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PersonService personService;


    @GetMapping("/person")
    public String personPage(@AuthenticationPrincipal SpringPerson springPerson, ModelMap modelMap) {
        List<Integer> allFriendRequests = personRepository.findAllFriendRequests(springPerson.getPerson().getId());
        List<Integer> allFriends = personRepository.findAllFriends(springPerson.getPerson().getId());
        List<Integer> all = new ArrayList<>(allFriends);
        all.addAll(personRepository.findAllFriendsSecond(springPerson.getPerson().getId()));
        List<Person> allById = personRepository.findAllById(allFriendRequests);
        List<Person> allFriend = personRepository.findAllById(all);

        modelMap.addAttribute("person", springPerson.getPerson());
        modelMap.addAttribute("persons", personRepository.findAll());
        modelMap.addAttribute("requests", allById);
        modelMap.addAttribute("friends", allFriend);
        return "personPage";
    }


    @GetMapping("/register")
    public String registerView() {
        return "register";
    }

    @PostMapping("/register")
    public String personRegister(@ModelAttribute Person person, @RequestParam("pic") MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File picture = new File(imageUploadDir + File.separator + fileName);
        file.transferTo(picture);
        person.setPicUrl(fileName);
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personRepository.save(person);
//        emailService.sendSimpleMessage(person.getEmail(), "Добро Пожаловать" + person.getName() , "Вы успешно зарегестрировались ");
        person.setActivationCode(UUID.randomUUID().toString());
        if (!StringUtils.isEmpty(person.getEmail())) {
            String text = String.format("Hello," + person.getName() +
                    "Welcome to Dobleface.Please, visit next link: http://localhost:8080/activate/" + person.getActivationCode());
            emailService.sendSimpleMessage(person.getEmail(), "Activation code", text);
        }
        return "redirect:register";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model,@PathVariable String code){
        boolean isActivated = personService.activatePerson(code);

        if (isActivated) {
            model.addAttribute("message", "Person successfully activated");
        }else {
            model.addAttribute("message", "Activation code is not found!");
        }
        return "login";
    }

    @GetMapping("/person/getImage")
    public void getImageAsByteArray(HttpServletResponse response, @RequestParam("picUrl") String picUrl) throws IOException {
        InputStream in = new FileInputStream(imageUploadDir + File.separator + picUrl);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }

    @GetMapping("/sendRequest")
    public String request(@RequestParam("id") int id, @AuthenticationPrincipal SpringPerson springPerson) {
        Person one = personRepository.getOne(id);
        if (one != null) {
            personRepository.saveFriendRequest(springPerson.getPerson().getId(), one.getId());
        }
        return "redirect:/person";
    }

    @GetMapping("/acceptOrReject")
    public String acceptOrReject(@RequestParam("id") int id, @RequestParam("action") String action, @AuthenticationPrincipal SpringPerson springPerson) {
        if (action.equals("accept") && personRepository.getOne(id) != null) {
            personRepository.addFriend(springPerson.getPerson().getId(), id);
            personRepository.removeRequest(id, springPerson.getPerson().getId());
        } else if (action.equals("reject") && personRepository.getOne(id) != null) {
            personRepository.removeRequest(id, springPerson.getPerson().getId());
        }
        return "redirect:/person";
    }


    @GetMapping("/remove")
    public String deleteFriend(@RequestParam("id") int id, @AuthenticationPrincipal SpringPerson springPerson) {
        personRepository.deleteFriendById(id, springPerson.getPerson().getId());
        personRepository.deletePersonFriendById(id, springPerson.getPerson().getId());
        return "redirect:/person";
    }

}