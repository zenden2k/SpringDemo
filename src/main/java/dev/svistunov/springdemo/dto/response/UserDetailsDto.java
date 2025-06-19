package dev.svistunov.springdemo.dto.response;

import dev.svistunov.springdemo.validation.annotations.ValidName;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDetailsDto {
    @NotNull
    private Long id;

    @NotBlank
    @ValidName
    private String firstName;

    @ValidName
    private String lastName;

    @ValidName
    private String middleName;

    @Email
    private String email;

    private String phoneNumber;

    private LocalDate birthDate;
}
