package com.example.demo.web;

import com.example.demo.entity.User;
import com.example.demo.exceptions.errors.BadRequestException;
import com.example.demo.exceptions.errors.UserNoActivate;
import com.example.demo.payload.request.SignInRequest;
import com.example.demo.payload.request.ResetPasswordRequset;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.payload.response.JWTTokenSuccessResponse;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.payload.response.ResetPasswordResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.UserService;
import com.example.demo.utils.ActivationCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;


@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
@Tag(name = "Authentication", description = "Authentication Description")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
        JWTTokenSuccessResponse login = userService.login(signInRequest);
        return ResponseEntity.ok(login);
    }


    @PostMapping("/signup")
    @Operation(
            description = "Endpoint for user registration",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "This is request body for user registration",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SignupRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "SignUp request",
                                            description = "SignUp request body",
                                            value = """
                                                    {
                                                      "email": "someExample@gmail.com",
                                                      "username": "string",
                                                      "password": "string"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully registered",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseEntity.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    description = "Response after success for request from web",
                                                    value = """
                                                                {
                                                                  "status" : "success",
                                                                  "message" : "user successfully registered"
                                                                }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User with this email or phone already exists",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ResponseEntity.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    value = """ 
                                                            {
                                                                "status" : "error",
                                                                "value" : null,
                                                                "message" : "user with this email address already exists"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    value = """ 
                                                            {
                                                                "status" : "error",
                                                                "value" : null,
                                                                "message" : "user with this phone already exists"
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation or verification error",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ResponseEntity.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Validation error",
                                                    value = """ 
                                                            {
                                                                "status" : "error",
                                                                "value" : null,
                                                                "message" : "Invalid credentials - Name should start with a capital letter and contain only letters, spaces, apostrophes, hyphens, and dots\\nName should start with a capital letter and contain only letters, spaces, apostrophes, hyphens, and dots\\nEmail should be valid\\nUser age must be between 18 and 80\\nPassword should have at least 6 characters\\nArmenian phone number length should be 12 characters"
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "Verification error",
                                                    value = """ 
                                                            {
                                                                "status" : "error",
                                                                "value" : null,
                                                                "message" : "This user is registered but not verified please. Check your email to verify account"
                                                            }
                                                            """
                                            )

                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ResponseEntity.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    value = """ 
                                                            
                                                            {
                                                                "status" : "error",
                                                                "value" : null,
                                                                "message" : "Internal server error"
                                                            }
                                                            
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        userService.createUser(signupRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }

    @GetMapping("/activate")
    @Operation(
            description = "Endpoint to activate user",
            parameters = {
                    @Parameter(
                            name = "code",
                            description = "Activation code",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully activated",
                            content = @Content(
                                    schema = @Schema(implementation = MessageResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    description = "Response after successfully activating the user",
                                                    value = "{\"message\": \"User is activated.\n<JWT_TOKEN>\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - User has already been activated",
                            content = @Content(
                                    schema = @Schema(implementation = BadRequestException.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"message\": \"User has already been activated. Please go to sign in.\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found - The user is not registered",
                            content = @Content(
                                    schema = @Schema(implementation = UserNoActivate.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = "{\"message\": \"The user is not registered\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<MessageResponse> activate(@RequestParam("code") String code) {
        return ResponseEntity.ok(userService.activateUser(code));
    }

    @PatchMapping ("/resetPassword")
    @Operation(
            description = "Endpoint to reset password",
            parameters = {
                    @Parameter(
                            name = "email",
                            description = "User's email",
                            required = true,
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password reset email sent successfully",
                            content = @Content(
                                    schema = @Schema(implementation = ResetPasswordResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    description = "Response after successfully sending the password reset email",
                                                    value = "{\"message\": \"Password reset email sent successfully.\", \"status\": \"OK\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Failed to reset password",
                            content = @Content(
                                    schema = @Schema(implementation = ResetPasswordResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Error",
                                                    description = "Response when failed to reset password",
                                                    value = "{\"message\": \"Failed to reset password: User not found.\", \"status\": \"BAD_REQUEST\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestParam("email") String email) {
        try {
            log.info(email, "deflfefefe");
            User user = userService.getUserByEmail(email);
//            user.setActivationCode(UUID.randomUUID().toString());
            user.setActivationCode(ActivationCode.generateRandomDigits());
            userService.sendMessageByEmailForResetPassword(user);
            userRepository.save(user);
            return ResponseEntity.ok(new ResetPasswordResponse("Password reset email sent successfully.", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResetPasswordResponse("Failed to reset password: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/changePassword")
    @Operation(
            description = "Endpoint to change user password",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request body for changing user password",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResetPasswordRequset.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User password changed successfully",
                            content = @Content(
                                    schema = @Schema(implementation = MessageResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Success",
                                                    description = "Response after successfully changing user password",
                                                    value = "{\"message\": \"User password changed. Please go to sign in.\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Password mismatch",
                            content = @Content(
                                    schema = @Schema(implementation = MessageResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Error",
                                                    description = "Response when password and confirm password do not match",
                                                    value = "{\"message\": \"Password not equals to confirm password\"}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Activate code not found",
                            content = @Content(
                                    schema = @Schema(implementation = MessageResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Error",
                                                    description = "Response when activate code is not found",
                                                    value = "{\"message\": \"Not found activate code\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ResetPasswordRequset resetPasswordRequset, BindingResult bindingResult) {
        if (!resetPasswordRequset.getPassword().equals(resetPasswordRequset.getConfirmPassword())) {
            return ResponseEntity.ok(new MessageResponse("Password not equals to confirm password"));
        }
        boolean isSetNewPassword = userService.setNewPassword(resetPasswordRequset);
        if (isSetNewPassword) {
            return ResponseEntity.ok(new MessageResponse("User password changed .Please go to sign in."));
        } else {
            return ResponseEntity.ok(new MessageResponse("Not found activate code"));
        }
    }
}
