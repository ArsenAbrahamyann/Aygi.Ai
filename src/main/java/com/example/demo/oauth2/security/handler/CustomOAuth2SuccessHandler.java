package com.example.demo.oauth2.security.handler;


import com.example.demo.entity.User;
import com.example.demo.entity.enums.ERole;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JWTTokenProvider;
import com.example.demo.security.SecurityConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor

public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JWTTokenProvider jwtTokenProvider;
    private final ApplicationContext applicationContext; // Add ApplicationContext

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setEmailVerified(true);
            user.getRoles().add(ERole.ROLE_USER);
            userRepository.save(user);
        }


        Authentication newAuth = new UsernamePasswordAuthenticationToken(email, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // Generate JWT token
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(newAuth);

        // Return JWT token in response
        response.setHeader("Authorization", jwt);
        response.getWriter().write("{\"token\": \"" + jwt + "\"}");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        // Redirect user after successful authentication
        response.sendRedirect("/");
    }

}
