package dev.svistunov.springdemo.services.interfaces;

import dev.svistunov.springdemo.dto.request.UserContactsInputDto;
import dev.svistunov.springdemo.dto.response.UserContactsDto;
import dev.svistunov.springdemo.model.User;

public interface UserContactsMapper {

    UserContactsDto toDto(User user);

    User toEntity(UserContactsInputDto dto);
}
