package com.anywr.test_spring_api.services;

import com.anywr.test_spring_api.dto.BearerToken;
import com.anywr.test_spring_api.dto.LoginDto;
import com.anywr.test_spring_api.dto.RegisterDto;
import com.anywr.test_spring_api.models.Role;
import com.anywr.test_spring_api.models.RoleName;
import com.anywr.test_spring_api.models.User;
import com.anywr.test_spring_api.repositories.IRoleRepository;
import com.anywr.test_spring_api.repositories.IUserRepository;
import com.anywr.test_spring_api.security.JwtUtilities;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final AuthenticationManager authenticationManager ;
    private final IUserRepository iUserRepository ;
    private final IRoleRepository iRoleRepository ;
    private final PasswordEncoder passwordEncoder ;
    private final JwtUtilities jwtUtilities ;

    @Override
    public ResponseEntity<?> register(RegisterDto registerDto) {

        if(iUserRepository.existsByUsername(registerDto.getUsername()))
            return  new ResponseEntity<>("username is already taken !", HttpStatus.SEE_OTHER);

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        //default role is User
        Role role = iRoleRepository.findByRoleName(RoleName.USER);
        user.setRoles(Collections.singletonList(role));
        iUserRepository.save(user);
        String token = jwtUtilities.generateToken(registerDto.getUsername(),Collections.singletonList(role.getRoleName().name()));
        return new ResponseEntity<>(new BearerToken(token , "Bearer "),HttpStatus.OK);
    }

    @Override
    public String authenticate(LoginDto loginDto) {
      Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = iUserRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<String> rolesNames = new ArrayList<>();
        user.getRoles().forEach(r-> rolesNames.add(r.getRoleName().name()));
        return jwtUtilities.generateToken(user.getUsername(),rolesNames);
    }

}

