package com.example.puyuan_demo.Registration;

import com.example.puyuan_demo.AppUser.AppUser;
import com.example.puyuan_demo.AppUser.AppUserRepository;
import com.example.puyuan_demo.AppUser.AppUserRole;
import com.example.puyuan_demo.Security.JwtService;
import com.example.puyuan_demo.auth.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponse register(RegistrationRequest request) {
        var user = AppUser.builder()
                .account(request.getAccount())
                .phone(request.getPhone())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .appUserRole(AppUserRole.USER)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
