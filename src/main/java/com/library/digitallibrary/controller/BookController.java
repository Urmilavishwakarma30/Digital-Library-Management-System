package com.library.digitallibrary.controller;

import com.library.digitallibrary.entity.Book;
import com.library.digitallibrary.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books";
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add-book";
    }

    @PostMapping("/save")
    public String saveBook(@ModelAttribute("book") Book book) {
        bookService.saveBook(book);
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "edit-book";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            Model model) {

        if (title != null && !title.isBlank()) {
            model.addAttribute("books", bookService.searchByTitle(title));
        } else if (author != null && !author.isBlank()) {
            model.addAttribute("books", bookService.searchByAuthor(author));
        } else if (category != null && !category.isBlank()) {
            model.addAttribute("books", bookService.findByCategory(category));
        } else {
            model.addAttribute("books", bookService.getAllBooks());
        }

        return "books";
    }
}