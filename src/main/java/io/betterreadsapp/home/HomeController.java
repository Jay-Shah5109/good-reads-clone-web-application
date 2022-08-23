package io.betterreadsapp.home;

import io.betterreadsapp.user.BooksByUser;
import io.betterreadsapp.user.BooksByUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    BooksByUserRepository booksByUserRepository;

    private final String COVER_IMG_ROOT = "http://covers.openlibrary.org/b/id/";

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model) {

        if (principal == null || principal.getAttribute("login") == null) {
            return "index";
        }

        String userID = principal.getAttribute("login");

//        System.out.println("UserID: "+userID);

        Slice<BooksByUser> booksSlice = booksByUserRepository
                .findAllById(userID, CassandraPageRequest.of(0,100));



        List<BooksByUser> booksByUser = booksSlice.getContent();
        booksByUser = booksByUser.stream().distinct().map(book -> {
            String coverImgURL = "/images/no_img.png";
            if (book.getCoverIds() != null & book.getCoverIds().size() > 0) {
                coverImgURL = COVER_IMG_ROOT + book.getCoverIds().get(0) + "-M.jpg";
            }
            book.setCoverUrl(coverImgURL);
            return book;
        }).collect(Collectors.toList());
//        System.out.println(booksByUser.toString());
        model.addAttribute("books", booksByUser);
        return "home";
    }
}
