package com.example.demo.web;

import com.example.demo.exceptions.errors.UserNoActivate;
import com.example.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/email-verification")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
@Transactional
public class EmailVerificationController {
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(EmailVerificationController.class.getName());

    @GetMapping("/{code}")
    public ResponseEntity<String> verifyEmail(@PathVariable("code") String verificationToken) {
        try {
            userService.activateUser(verificationToken);
            logger.info("User account activated successfully.");
            return ResponseEntity.ok("User activated successfully!");
        } catch (UserNoActivate e) {
            logger.warn("Error activating user account: " + e.getMessage());
            return ResponseEntity.badRequest().body("User not activated! Please connect with your administrator.");
        } catch (Exception ex) {
            logger.warn("Error activating user account: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error!");
        }
    }
}



