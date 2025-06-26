package dev.svistunov.springdemo.services.interfaces;

import dev.svistunov.springdemo.dto.request.UserDetailsInputDto;
import dev.svistunov.springdemo.dto.response.UserDetailsDto;
import dev.svistunov.springdemo.model.User;

public interface UserDetailsMapper {

    UserDetailsDto toDto(User user);

    User toEntity(UserDetailsInputDto dto);
}
