package com.anywr.test_spring_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterDto {
    @NotNull
    @Email
    String email ;
    String firstName ;
    String lastName ;
    @NotNull
    @Size(min = 3, max = 50)
    String username;
    @NotNull
    @NotBlank
    @Size(min = 4, max = 100)
    String password ;
}
