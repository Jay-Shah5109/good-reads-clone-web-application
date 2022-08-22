package io.betterreadsapp.book;

import io.betterreadsapp.userbooks.UserBooks;
import io.betterreadsapp.userbooks.UserBooksPrimaryKey;
import io.betterreadsapp.userbooks.UserBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    @Autowired
    UserBooksRepository userBooksRepository;

    @GetMapping(value = "/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model, @AuthenticationPrincipal OAuth2User principal) {
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            String coverImgURL = "/images/no_img.png";
            if (book.getCoverIDs() != null && book.getCoverIDs().size() > 0) {
                coverImgURL = COVER_IMG_ROOT + book.getCoverIDs().get(0) + "-L.jpg";
            }
            model.addAttribute("coverImage", coverImgURL);
            model.addAttribute("book", book);
            if (principal != null && principal.getAttribute("login") != null) {
                String userID = principal.getAttribute("login");
                model.addAttribute("loginID", userID);
                UserBooksPrimaryKey key = new UserBooksPrimaryKey();
                key.setBookID(bookId);
                key.setUserID(userID);
                Optional<UserBooks> userBooks = userBooksRepository.findById(key);
                if (userBooks.isPresent()) {
                    model.addAttribute("userBooks", userBooks.get());
                } else {
                    model.addAttribute("userBooks", new UserBooks());
                }
            }
            return "book";
        }
        return "book_not_found";
    }


}
