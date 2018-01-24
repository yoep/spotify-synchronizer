package be.studios.yoep.spotify.synchronizer.authorization;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @GetMapping("/login")
    @ResponseBody
    public String login() {
        return "test";
    }
}
