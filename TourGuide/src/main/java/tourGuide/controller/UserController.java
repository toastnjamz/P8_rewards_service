package tourGuide.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tourGuide.domain.user.UserPreferences;
import tourGuide.service.UserService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @GetMapping("/all-user-names")
    public List<String> getAllUsersNames() {
        return userService.getAllUsersNames();
    }

    @GetMapping("/user-preferences")
    public UserPreferences getUserPreferences(@RequestParam String userName) {
        return userService.getUserPreferences(userName);
    }

    //TODO
    @PostMapping("/user-preferences")
    public void setUserPreferences(@RequestParam String userName, @RequestBody UserPreferences userPreferences,
                                   HttpServletResponse response) {
        userService.setUserPreferences(userName, userPreferences);
        response.setStatus(201);
    }
}
