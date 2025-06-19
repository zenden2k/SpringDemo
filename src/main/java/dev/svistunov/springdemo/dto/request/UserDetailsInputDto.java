package dev.svistunov.springdemo.dto.request;

import dev.svistunov.springdemo.validation.annotations.ValidBirthDate;
import dev.svistunov.springdemo.validation.annotations.ValidName;
import dev.svistunov.springdemo.validation.annotations.ValidPhoneNumber;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDetailsInputDto {
    @NotBlank
    @ValidName
    @NotNull
    @Size(max = 100, message = "Имя должно быть не длиннее 100 символов")
    private String firstName;

    @ValidName
    @Size(max = 100, message = "Имя должно быть не длиннее 100 символов")
    private String lastName;

    @ValidName
    @Size(max = 100, message = "Имя должно быть не длиннее 100 символов")
    private String middleName;

    @Email
    private String email;

    @ValidPhoneNumber
    private String phoneNumber;

    @ValidBirthDate
    private LocalDate birthDate;
}
