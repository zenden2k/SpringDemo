package dev.svistunov.springdemo.dto.request;

public record UserContactsSearchDto (
        Long id,
        String firstName,
        String email,
        String phoneNumber
) {}