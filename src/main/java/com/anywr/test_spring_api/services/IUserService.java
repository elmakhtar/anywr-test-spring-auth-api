package com.anywr.test_spring_api.services;


import com.anywr.test_spring_api.dto.LoginDto;
import com.anywr.test_spring_api.dto.RegisterDto;

import org.springframework.http.ResponseEntity;


public interface IUserService {

   String authenticate(LoginDto loginDto);
   ResponseEntity<?> register (RegisterDto registerDto);
}
