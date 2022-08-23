package io.betterreadsapp.userbooks;

import io.betterreadsapp.book.Book;
import io.betterreadsapp.book.BookRepository;
import io.betterreadsapp.user.BooksByUser;
import io.betterreadsapp.user.BooksByUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class UserBooksController {

    @Autowired
    UserBooksRepository userBooksRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BooksByUserRepository booksByUserRepository;


    @PostMapping("/addUserBook")
    public ModelAndView addBookForUser(@RequestBody MultiValueMap<String, String> formData,
                                       @AuthenticationPrincipal OAuth2User principal) {

        if (principal == null && principal.getAttribute("login") == null) {
            return null;
        }

        UserBooks userBooks = new UserBooks();
        UserBooksPrimaryKey key = new UserBooksPrimaryKey();

        String userID = principal.getAttribute("login");

        key.setUserID(userID);
        String bookID = formData.getFirst("bookId"); // hidden property mentioned in book.html
        Optional<Book> optionalBook = bookRepository.findById(bookID);
        if (!optionalBook.isPresent()) {
            return new ModelAndView("redirect:/");
        }
        Book book = optionalBook.get();

        key.setBookID(bookID);

        userBooks.setKey(key);

        int rating = Integer.parseInt(formData.getFirst("rating"));

        userBooks.setStartedDate(LocalDate.parse(formData.getFirst("startedDate")));
        userBooks.setCompletedDate(LocalDate.parse(formData.getFirst("completedDate")));
        userBooks.setRating(Integer.parseInt(formData.getFirst("rating")));
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));

        userBooksRepository.save(userBooks);

        BooksByUser booksByUser = new BooksByUser();
        booksByUser.setId(userID);
        booksByUser.setBookId(bookID);
        booksByUser.setBookName(book.getName());
        booksByUser.setCoverIds(book.getCoverIDs());
        booksByUser.setAuthorNames(book.getAuthorNames());
        booksByUser.setReadingStatus(formData.getFirst("readingStatus"));
        booksByUser.setRating(rating);
        booksByUserRepository.save(booksByUser);


        System.out.println(formData);
        return new ModelAndView("redirect:/books/" + bookID);
    }

}
