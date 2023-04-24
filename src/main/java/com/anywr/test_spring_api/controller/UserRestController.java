package com.anywr.test_spring_api.controller;

import com.anywr.test_spring_api.dto.LoginDto;
import com.anywr.test_spring_api.dto.RegisterDto;
import com.anywr.test_spring_api.services.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {
    private final IUserService iUserService ;

    @PostMapping("/register")
    public ResponseEntity<?> register (@Valid @RequestBody RegisterDto registerDto) {
        return  iUserService.register(registerDto);
    }

    @PostMapping("/login")
    public String authenticate(@Valid @RequestBody LoginDto loginDto) {
        return  iUserService.authenticate(loginDto);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<UserDetails> userDetails(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getDetails();
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }

}
