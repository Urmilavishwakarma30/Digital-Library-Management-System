package com.library.digitallibrary.controller;

import com.library.digitallibrary.service.BorrowService;
import com.library.digitallibrary.service.UserQueryService;
import com.library.digitallibrary.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    private final BorrowService borrowService;

    private final UserRepository userRepository;

    private final UserQueryService userQueryService;

    public AdminController(BorrowService borrowService,
                           UserRepository userRepository,
                           UserQueryService userQueryService) {

        this.borrowService = borrowService;
        this.userRepository = userRepository;
        this.userQueryService = userQueryService;
    }

    @GetMapping("/admin")
    public String adminDashboard() {

        return "admin-dashboard";
    }

    @GetMapping("/admin/issued-books")
    public String viewIssuedBooks(Model model) {

        model.addAttribute(
            "borrowRecords",
            borrowService.getIssuedBooks()
        );

        return "admin-issued-books";
    }

    @GetMapping("/admin/members")
    public String viewMembers(Model model) {

        model.addAttribute(
            "members",
            userRepository.findAll()
        );

        return "admin-members";
    }

    @GetMapping("/admin/fines")
    public String viewFines(Model model) {

        model.addAttribute(
            "fineRecords",
            borrowService.getFineRecords()
        );

        return "admin-fines";
    }

    @PostMapping("/admin/fines/pay")
    public String markFinePaid(@RequestParam Long id) {

        borrowService.markFinePaid(id);

        return "redirect:/admin/fines";
    }

    @PostMapping("/admin/members/delete")
    public String deleteMember(@RequestParam Long id) {

        userRepository.findById(id).ifPresent(user -> {

            // Admin account ko delete nahi karna
            if (!"ADMIN".equals(user.getRole())) {
                userRepository.deleteById(id);
            }

        });

        return "redirect:/admin/members";
    }

    // View User Queries
    @GetMapping("/admin/queries")
    public String viewQueries(Model model) {

        model.addAttribute(
            "queries",
            userQueryService.getAllQueries()
        );

        return "admin-queries";
    }
}