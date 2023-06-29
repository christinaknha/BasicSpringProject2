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
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;

    @Autowired
    public BookController(BookRepository bookRepo, AuthorRepository authorRepo) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
    }

    @GetMapping("/")
    public String listBooks(Model model) {
        List<Book> books = bookRepo.findAll();
        model.addAttribute("books", books);
        return "BookList";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("author", new Author());
        return "BookCreate";
    }

    @PostMapping("/create")
    public String createBook(@ModelAttribute("book") Book book) {
        Author author = book.getAuthor();
        Author existingAuthor = authorRepo.findAuthorByName(author.getName());

        if (existingAuthor != null) {
            // Update existing author
            book.setAuthor(existingAuthor);
            existingAuthor.getBooks().add(book);

        } else {
            // Create a new author
            author.getBooks().add(book);
            book.setAuthor(author);

        }
        bookRepo.save(book);
        return "redirect:/books/";
    }

    @GetMapping("/update-{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Book book = bookRepo.findById(id).orElse(null);
        if (book != null) {
            model.addAttribute("book", book);
            model.addAttribute("author", new Author());
            return "BookUpdate";
        }
        return "redirect:/books/";
    }

    @PostMapping("/update-{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book updatedBook) {
        Book book = bookRepo.findById(id).orElse(null);
        if (book != null) {
            book.setTitle(updatedBook.getTitle());
            Author updatedAuthor = updatedBook.getAuthor();

            Author existingAuthor = authorRepo.findById(updatedBook.getId()).orElse(null);
            if (existingAuthor != null) {
                existingAuthor.setName(updatedAuthor.getName());
                authorRepo.save(existingAuthor);
            }
            bookRepo.save(book);
        }
        return "redirect:/books/";
    }

    @PostMapping("/{id}")
    public String deleteBook(@PathVariable Long id) {
        Book book = bookRepo.findById(id).orElse(null);
        if (book != null) {
            // Remove book from the author's list of books
            Author author = book.getAuthor();
            author.getBooks().remove(book);
            authorRepo.save(author);

            // Delete the book
            bookRepo.delete(book);
        }
        return "redirect:/books/";
    }

}
