package com.example.demo.oauth2;


import com.example.demo.repository.UserRepository;
import com.example.demo.security.JWTTokenProvider;
import com.example.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
@RequiredArgsConstructor
public class Oauth2Controller {
    private final UserRepository userRepository;
    private  final UserService userService;
    private final JWTTokenProvider tokenProvider;

    @GetMapping("/signInWithGoogle")
    public String signInWithGoogle() {
        return "redirect:   http://api.aygi.ai/oauth2/authorization/google ";
    }

}

