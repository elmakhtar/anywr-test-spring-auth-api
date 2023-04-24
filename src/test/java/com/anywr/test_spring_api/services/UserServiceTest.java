package com.anywr.test_spring_api.services;

import com.anywr.test_spring_api.dto.RegisterDto;
import com.anywr.test_spring_api.models.Role;
import com.anywr.test_spring_api.models.RoleName;
import com.anywr.test_spring_api.repositories.IRoleRepository;
import com.anywr.test_spring_api.repositories.IUserRepository;
import com.anywr.test_spring_api.security.JwtUtilities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockitoExtension.class})
class UserServiceTest {

    @Mock
    private AuthenticationManager authenticationManager ;
    @Mock
    private IUserRepository iUserRepository ;
    @Mock
    private IRoleRepository iRoleRepository ;
    @Mock
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private JwtUtilities jwtUtilities ;

    @InjectMocks
    private UserService userService;

    private static final String USER_ONE_USERNAME = "test-user-one";
    private static final String USER_ONE_EMAIL = "test-user-one@localhost";
    private static final String USER_TWO_USERNAME = "test-user-two";
    private static final String USER_TWO_EMAIL = "test-user-two@localhost";
    private static final String USER_THREE_USERNAME = "test-user-three";
    private static final String USER_THREE_EMAIL = "test-user-three@localhost";
    private static final Role roleUser = Role.builder().roleName(RoleName.USER).build();

    @Test
    void assertThatUSerCanRegister() {
        Mockito.when(iRoleRepository.findByRoleName(RoleName.USER)).thenReturn(roleUser);
        RegisterDto registerDto = RegisterDto.builder()
                .username(USER_ONE_USERNAME)
                .email(USER_ONE_EMAIL)
                .password("test-password")
                .build();
        ResponseEntity<?> response = userService.register(registerDto);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertThat(response.getBody()).asString().contains("Bearer ");
    }

    @Test
    void assertThatUsernameIsUnique() {
        Mockito.when(iUserRepository.existsByUsername(USER_TWO_USERNAME)).thenReturn(true);
        RegisterDto registerDto = RegisterDto.builder()
                .username(USER_TWO_USERNAME)
                .email(USER_TWO_EMAIL)
                .password("test-password")
                .build();
        ResponseEntity<?> response = userService.register(registerDto);
        assertTrue(response.getStatusCode().is3xxRedirection());
        assertThat(response.getBody()).asString().contains("username is already taken");
    }
}