package com.hireflow.auth.controller;

import com.hireflow.auth.dto.AuthResponse;
import com.hireflow.auth.dto.LoginRequest;
import com.hireflow.auth.dto.RegisterRequest;
import com.hireflow.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validate() {
        // Gateway validates JWT before reaching here; just confirm OK
        return ResponseEntity.ok("Token valid");
    }
}
