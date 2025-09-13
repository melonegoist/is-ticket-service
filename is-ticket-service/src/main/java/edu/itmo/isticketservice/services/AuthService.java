package edu.itmo.isticketservice.services;

import edu.itmo.isticketservice.dto.JwtResponse;
import edu.itmo.isticketservice.dto.SignInRequest;
import edu.itmo.isticketservice.dto.SignUpRequest;
import edu.itmo.isticketservice.model.Role;
import edu.itmo.isticketservice.model.User;
import edu.itmo.isticketservice.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse signUp(SignUpRequest signUpRequest) {
        var user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password("/ todo")
                .role(Role.ROLE_AUTHORISED_USER)
                .build();

        userService.createUser(user);

        var jwtToken = jwtUtils.generateToken(user);

        return new JwtResponse(jwtToken, 220, "jwt token generated successfully");
    }

    public JwtResponse signIn(SignInRequest signInRequest) {
        var user = userService.getUserByUsername(signInRequest.getUsername());

        if (user.getPassword().equals(signInRequest.getPassword())) {
            var jwtToken = jwtUtils.generateToken(user);
            return new JwtResponse(jwtToken, 220, "jwt token generated successfully");
        }

        throw new RuntimeException("Invalid username or password");

        // todo
    }

}
