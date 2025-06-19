package dev.svistunov.springdemo.dto.request;

import java.time.LocalDate;

public record UserDetailsSearchDto(
        Long id,
        String firstName,
        String lastName,
        String middleName,
        LocalDate birthDateFrom,
        LocalDate birthDateTo,
        String email,
        String phoneNumber
) {}