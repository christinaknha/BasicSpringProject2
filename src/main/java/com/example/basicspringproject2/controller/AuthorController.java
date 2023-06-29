package com.example.basicspringproject2.controller;

import com.example.basicspringproject2.model.Author;
import com.example.basicspringproject2.model.Book;
import com.example.basicspringproject2.repository.AuthorRepository;
import com.example.basicspringproject2.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorRepository authorRepo;
    private final BookRepository bookRepo;

    @Autowired
    public AuthorController(AuthorRepository authorRepo, BookRepository bookRepo) {
        this.authorRepo = authorRepo;
        this.bookRepo = bookRepo;
    }

    @GetMapping("/")
    public String listAuthors(Model model) {
        List<Author> authors = authorRepo.findAll();
        model.addAttribute("authors", authors);
        return "AuthorList";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("author", new Author());
        model.addAttribute("book", new Book());
        return "AuthorCreate";
    }

    @PostMapping("/create")
    public String createAuthor(@ModelAttribute("author") Author author, @ModelAttribute("book") Book book) {
        Author existingAuthor = authorRepo.findAuthorByName(author.getName());
        if (existingAuthor != null) {
            // Update existing author with new book
            book.setAuthor(existingAuthor);
            existingAuthor.getBooks().add(book);
            authorRepo.save(existingAuthor);
        } else {
            // Create a new author
            book.setAuthor(author);
            author.getBooks().add(book);
            authorRepo.save(author);
        }
        return "redirect:/authors/";
    }

    @GetMapping("/update-{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Author author = authorRepo.findById(id).orElse(null);
        if (author != null) {
            model.addAttribute("author", author);
            model.addAttribute("book", new Book());
            return "AuthorUpdate";
        }
        return "redirect:/authors/";
    }


    @PostMapping("/{id}")
    public String deleteAuthor(@PathVariable Long id) {
        Author author = authorRepo.findById(id).orElse(null);
        if (author != null) {
            // Remove all books associated with the author
            List<Book> books = author.getBooks();
            for (Book book : books) {
                book.setAuthor(null);
                bookRepo.save(book);
            }

            // Delete the author
            authorRepo.delete(author);
        }
        return "redirect:/authors/";
    }
}


