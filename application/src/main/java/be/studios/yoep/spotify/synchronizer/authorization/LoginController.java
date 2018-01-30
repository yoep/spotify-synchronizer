package be.studios.yoep.spotify.synchronizer.authorization;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    public static final String ENDPOINT = "/login";

    @GetMapping(ENDPOINT)
    @ResponseBody
    public String login() {
        return "test login";
    }

    @GetMapping("/authorized")
    @ResponseBody
    public String authorized() {
        return "test authorized";
    }
}
