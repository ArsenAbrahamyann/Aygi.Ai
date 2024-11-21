package com.example.demo.services;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserMapper;
import com.example.demo.entity.FriendRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.ERole;
import com.example.demo.exceptions.errors.BadRequestException;
import com.example.demo.exceptions.errors.NotFoundException;
import com.example.demo.exceptions.errors.UserExistException;
import com.example.demo.exceptions.errors.UserNoActivate;
import com.example.demo.payload.request.SignInRequest;
import com.example.demo.payload.request.ResetPasswordRequset;
import com.example.demo.payload.request.SignupRequest;
import com.example.demo.payload.request.UserUpdate;
import com.example.demo.payload.response.JWTTokenSuccessResponse;
import com.example.demo.payload.response.MessageResponse;
import com.example.demo.repository.FriendRequestRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JWTTokenProvider;
import com.example.demo.security.SecurityConstants;
import com.example.demo.utils.ActivationCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.demo.services.CommentService.LOG;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private final EmailServiceImpl emailService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final FriendRequestRepository friendRequestRepository;


    private final UserMapper userMapper;

    @Value("${hostname}")
    private String hostname;
    @Value("${email.activate.url}")
    private String emailActivateUrl;

    public static User build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        return new User(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }


    @Transactional(readOnly = true)
    public User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        if (userRepository.findUserByUsername(username).isEmpty()) {

            return userRepository.findUserByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));

        } else {
            return userRepository.findUserByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username not found with username " + username));
        }

    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found with username: " + username));

        return build(user);
    }

    public User loadUserById(int id) {
        return userRepository.findUserById(id).orElse(null);
    }

    public User createUser(SignupRequest userIn) {
        userRepository.findUserByEmail(userIn.getEmail()).ifPresent(UserExistException::new);
        User user = new User();
        user.setEmail(userIn.getEmail());
        user.setUsername(userIn.getUsername());
        user.setPassword(passwordEncoder.encode(userIn.getPassword()));
        user.getRoles().add(ERole.ROLE_USER);


        user.setEnabled(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);

        // user.setActivationCode(UUID.randomUUID().toString());

        user.setActivationCode(ActivationCode.generateRandomDigits());


        try {
            user = userRepository.save(user);
            log.info("Saving User {}", userIn.getEmail());
        } catch (Exception e) {
            log.error("Error during registration. {}", e.getMessage());
            throw new UserExistException("The user " + user.getUsername() + " already exist. Please check credentials");
        }

        sendActivateMessage(user);
        log.info("The user " + user.getUsername() + " send message for activate.");
        return user;
    }

    private void sendActivateMessage(User user) {
        if (! StringUtils.isEmpty(user.getEmail())) {
            String subject = "Aygi.ai: Activation code";

            String message = String.format(
                    "Hello, %s\n"
                            +
                            "Welcome to Aygi.ai! Your activation code: %s",
                    user.getUsername(),
                    user.getActivationCode()
            );

            emailService.sendMessageToEmail(user.getEmail(), subject, message);
        }
    }

    public void sendMessageByEmailForResetPassword(User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Activation code for reset password:  %s ",
                    user.getActivationCode()
            );
            emailService.sendMessageToEmail(user.getEmail(), "Reset Password for Agro", message);
        }
    }


    public User updateUser(@Valid UserUpdate userDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        user.setBio(userDTO.getBio());
        user.setUsername(userDTO.getUsername());
        return userRepository.save(user);
    }

    public MessageResponse activateUser(String code) {
        User user = userRepository.findUserByActivationCode(code).orElseThrow(() -> new UserNoActivate("The user is not registered"));
        if (!user.isActive()) {
            user.setActive(true);
            user.setActivationCode(null);
            userRepository.save(user);

            String token = jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
            log.info("Generated JWT Token: {}", token);
            return new MessageResponse("User is activated!.\n" + "Token '" + token + " '");
        } else if (user.isActive()) {
            String token = jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
            return new MessageResponse("User is activated!.\n" + "Token '" + token + " '");
        }else {
            throw new BadRequestException("User has already been activated. Please go to sign in.");
        }
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            LOG.error("JWT token validation failed: {}", ex.getMessage());
            return false;
        }
    }

    public int getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.SECRET)
                .parseClaimsJws(token)
                .getBody();
        String id = (String) claims.get("id");
        return Integer.parseInt(id);
    }



    public boolean setNewPassword(ResetPasswordRequset resetPasswordRequset) {
        try {
            User user = userRepository.findUserByActivationCode(resetPasswordRequset.getCode()).orElseThrow(() -> new UserNoActivate("Activate code not found"));
            user.setActivationCode(null);
            user.setPassword(passwordEncoder.encode(resetPasswordRequset.getPassword()));
            userRepository.save(user);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public UserDTO getCurrentUser(Principal principal) {
        return modelMapper.map(getUserByPrincipal(principal), UserDTO.class);
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public JWTTokenSuccessResponse login(SignInRequest signInRequest) {
        if (!getUserByEmail(signInRequest.getEmail()).isActive()) {
            throw new BadRequestException("The user is inactive.");
        }
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInRequest.getEmail(),
                    signInRequest.getPassword()
            ));

            log.info("Authentication" + authentication.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

        return new JWTTokenSuccessResponse(jwt);
    }


    public void sendFriendRequest(Integer senderId, Integer receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        FriendRequest friendRequest = new FriendRequest(null, sender, receiver, LocalDateTime.now(), "pending");
        sender.getSentFriendRequests().add(friendRequest);
        userRepository.save(sender);
    }

    public void cancelFriendRequest(Integer senderId, Integer receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Convert receiverId to int before comparison
        int receiverIdInt = receiverId.intValue();

        sender.getSentFriendRequests().removeIf(request -> request.getReceiver().getId() == receiverIdInt);
        userRepository.save(sender);
    }


    public void acceptFriendRequest(Integer requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        request.getReceiver().getReceivedFriendRequests().remove(request);
        request.getReceiver().getFriends().add(request.getSender());
        userRepository.save(request.getReceiver());
        friendRequestRepository.delete(request);
    }

    public void declineFriendRequest(Integer requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        request.getReceiver().getReceivedFriendRequests().remove(request);
        friendRequestRepository.delete(request);
    }


    public UserDTO getUserDTOById(int id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(userMapper::toUserDTO).orElseThrow(() -> new NotFoundException("User not found"));
    }


    public UserDTO getCurrentUser2(Principal principal) {
        Optional<User> user = getUserByPrincipalOptional(principal);

        return user.map(userMapper::toUserDTO).orElseThrow(() -> new NotFoundException("User not found"));
    }


    public Optional<User> getUserByPrincipalOptional(Principal principal) {
        String username = principal.getName();
        if (userRepository.findUserByUsername(username).isEmpty()) {
            return userRepository.findUserByEmail(username);
        } else {
            return userRepository.findUserByUsername(username);
        }

    }

}
