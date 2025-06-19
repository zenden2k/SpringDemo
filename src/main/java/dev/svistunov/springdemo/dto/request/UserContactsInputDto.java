package dev.svistunov.springdemo.dto.request;

import dev.svistunov.springdemo.validation.annotations.ValidName;
import dev.svistunov.springdemo.validation.annotations.ValidPhoneNumber;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserContactsInputDto {
    @NotBlank
    @Size(max = 100, message = "Имя должно быть не длиннее 100 символов")
    @ValidName
    private String firstName;

    @Email
    private String email;

    @ValidPhoneNumber
    private String phoneNumber;
}
