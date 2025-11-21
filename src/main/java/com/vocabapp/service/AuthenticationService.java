package com.vocabapp.service;

import com.vocabapp.dto.AuthResponse;
import com.vocabapp.dto.LoginRequest;
import com.vocabapp.dto.RegisterRequest;
import com.vocabapp.model.RefreshToken;
import com.vocabapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    
    public AuthResponse register(RegisterRequest request) {
        User user = userService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
        
        String token = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user.getId());
        
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .username(user.getUsername())
                .email(user.getEmail())
                .userId(user.getId())
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );
        
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user.getId());
        
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .username(user.getUsername())
                .email(user.getEmail())
                .userId(user.getId())
                .build();
    }
}
