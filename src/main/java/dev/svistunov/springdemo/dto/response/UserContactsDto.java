package dev.svistunov.springdemo.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class UserContactsDto {
    @NotNull
    private Long id;

    @NotBlank
    private String firstName;

    @Email
    private String email;

    private String phoneNumber;
}
