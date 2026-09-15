package com.library.digitallibrary.controller;

import com.library.digitallibrary.entity.User;
import com.library.digitallibrary.entity.UserQuery;
import com.library.digitallibrary.repository.UserRepository;
import com.library.digitallibrary.service.UserQueryService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserQueryController {

    private final UserQueryService userQueryService;
    private final UserRepository userRepository;

    public UserQueryController(UserQueryService userQueryService,
                               UserRepository userRepository) {
        this.userQueryService = userQueryService;
        this.userRepository = userRepository;
    }

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("query", new UserQuery());
        return "contact";
    }

    @PostMapping("/contact")
    public String submitQuery(UserQuery query,
                              Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        userQueryService.saveQuery(query, user);

        return "redirect:/contact?success";
    }
}