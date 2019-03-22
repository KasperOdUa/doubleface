package com.example.doubleface.controller;


import com.example.doubleface.model.Message;
import com.example.doubleface.model.Person;
import com.example.doubleface.repository.MessageRepository;
import com.example.doubleface.repository.PersonRepository;
import com.example.doubleface.security.SpringPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PersonRepository personRepository;

    private Person person;

    @GetMapping("/messagePage")
    public String messagePage(@RequestParam("id") int id, ModelMap modelMap) {
        List<Message> messages = new ArrayList<>(messageRepository.findAllMessagesById(id));
        messages.addAll(messageRepository.findAllMessagesByIdSecond(id));
        person = personRepository.getOne(id);
        modelMap.addAttribute("messages", messages);
        modelMap.addAttribute("toId", person);
        return "messagePage";
    }

    @PostMapping("/sendMessage")
    public String message(@ModelAttribute Message message, @AuthenticationPrincipal SpringPerson springPerson, RedirectAttributes redirectAttributes) {
        message.setFromId(springPerson.getPerson());
        message.setToId(person);
        message.setDate(new Date());
        messageRepository.save(message);
        redirectAttributes.addAttribute("id", person.getId());
        return "redirect:/messagePage";
    }
}
