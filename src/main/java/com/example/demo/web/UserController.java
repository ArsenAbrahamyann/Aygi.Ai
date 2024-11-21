package com.example.demo.web;

import com.example.demo.dto.FriendRequestDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.payload.request.UserUpdate;
import com.example.demo.services.EmailServiceImpl;
import com.example.demo.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("api/user")
@CrossOrigin
@Tag(name = "User", description = "User Description")
@RequiredArgsConstructor
public class UserController {
    private final EmailServiceImpl emailService;
    private final UserService userService;
    private final ModelMapper modelMapper;


    @GetMapping("/")
    @Operation(
            description = "Endpoint to get current user information",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Current user information retrieved successfully",
                            content = @Content(
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - User not authenticated",
                            content = @Content(
                                    schema = @Schema(implementation = Error.class)
                            )
                    )
            }
    )
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
//        UserDTO userDTO = userService.getCurrentUser(principal);
        UserDTO userDTO = userService.getCurrentUser2(principal);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    @Operation(
            description = "Endpoint to get user profile by user ID",
            security = {@SecurityRequirement(name = "bearerAuth")},
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "ID of the user",
                            required = true,
                            in = ParameterIn.PATH,
                            schema = @Schema(type = "integer", format = "int32")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profile retrieved successfully",
                            content = @Content(
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found - The requested user ID does not exist",
                            content = @Content(
                                    schema = @Schema(implementation = Error.class)
                            )
                    )
            }
    )
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") int userId) {
        UserDTO userDTO = userService.getUserDTOById(userId);
//        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/")
    @Operation(
            description = "Endpoint to update user information",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request body for updating user information",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserUpdate.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User information updated successfully",
                            content = @Content(
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - Invalid input data",
                            content = @Content(
                                    schema = @Schema(implementation = Error.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - User not authenticated",
                            content = @Content(
                                    schema = @Schema(implementation = Error.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found - User not found",
                            content = @Content(
                                    schema = @Schema(implementation = Error.class)
                            )
                    )
            }
    )
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserUpdate userDTO, Principal principal) {


        User user = userService.updateUser(userDTO, principal);

        UserDTO userUpdated = modelMapper.map(user, UserDTO.class);
        return new ResponseEntity<>(userUpdated, HttpStatus.OK);
    }

    @PostMapping("/users/{senderId}/send-friend-request/{receiverId}")
    @Operation(
            description = "Send a friend request",
            security = {@SecurityRequirement(name = "bearerAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request body for sending a friend request",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FriendRequestDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Send Friend Request",
                                            description = "Send friend request request body",
                                            value = """
                                            {
                                                "senderId": "string",
                                                "receiverId": "string"
                                            }
                                            """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Friend request sent successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    public ResponseEntity<?> sendFriendRequest(@PathVariable("senderId") Integer senderId,
                                               @PathVariable("receiverId") Integer receiverId) {
        userService.sendFriendRequest(senderId, receiverId);
        return ResponseEntity.ok("Friend request sent successfully.");
    }

    @PostMapping("/users/{senderId}/cancel-friend-request/{receiverId}")
    @Operation(
            description = "Cancel a friend request",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Friend request canceled successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    public ResponseEntity<?> cancelFriendRequest(@PathVariable("senderId") Integer senderId,
                                                 @PathVariable("receiverId") Integer receiverId) {
        userService.cancelFriendRequest(senderId, receiverId);
        return ResponseEntity.ok("Friend request canceled successfully.");
    }

    @PostMapping("/users/accept-friend-request/{requestId}")
    @Operation(
            description = "Accept a friend request",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Friend request accepted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Friend request not found"
                    )
            }
    )
    public ResponseEntity<?> acceptFriendRequest(@PathVariable("requestId") Integer requestId) {
        userService.acceptFriendRequest(requestId);
        return ResponseEntity.ok("Friend request accepted successfully.");
    }

    @PostMapping("/users/decline-friend-request/{requestId}")
    @Operation(
            description = "Decline a friend request",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Friend request declined successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Friend request not found"
                    )
            }
    )
    public ResponseEntity<?> declineFriendRequest(@PathVariable("requestId") Integer requestId) {
        userService.declineFriendRequest(requestId);
        return ResponseEntity.ok("Friend request declined successfully.");
    }
}
