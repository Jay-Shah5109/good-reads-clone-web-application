package io.betterreadsapp.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class BookController {

    private final String COVER_IMG_ROOT = "http://covers.openlibrary.org/b/id/";
    @Autowired
    BookRepository bookRepository;

    @GetMapping(value = "/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            String coverImgURL = "/images/no_img.png";
            if (book.getCoverIDs() != null && book.getCoverIDs().size() > 0) {
                coverImgURL = COVER_IMG_ROOT + book.getCoverIDs().get(0) + "-L.jpg";
            }
            model.addAttribute("coverImage", coverImgURL);
            model.addAttribute("book", book);
            return "book";
        }
        return "book_not_found";
    }


}
