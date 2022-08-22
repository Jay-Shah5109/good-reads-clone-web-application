package io.betterreadsapp.userbooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

@Controller
public class UserBooksController {

    @Autowired
    UserBooksRepository userBooksRepository;


    @PostMapping("/addUserBook")
    public ModelAndView addBookForUser(@RequestBody MultiValueMap<String, String> formData,
                                       @AuthenticationPrincipal OAuth2User principal) {

        if (principal == null && principal.getAttribute("login") == null) {
            return null;
        }

        UserBooks userBooks = new UserBooks();
        UserBooksPrimaryKey key = new UserBooksPrimaryKey();

        key.setUserID(principal.getAttribute("login"));
        String bookID = formData.getFirst("bookId"); // hidden property mentioned in book.html
        key.setBookID(bookID);

        userBooks.setKey(key);

        userBooks.setStartedDate(LocalDate.parse(formData.getFirst("startedDate")));
        userBooks.setCompletedDate(LocalDate.parse(formData.getFirst("completedDate")));
        userBooks.setRating(Integer.parseInt(formData.getFirst("rating")));
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));

        userBooksRepository.save(userBooks);

        System.out.println(formData);
        return new ModelAndView("redirect:/books/" + bookID);
    }

}
