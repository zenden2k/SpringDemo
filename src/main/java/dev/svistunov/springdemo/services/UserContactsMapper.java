package dev.svistunov.springdemo.services;

import dev.svistunov.springdemo.dto.response.UserContactsDto;
import dev.svistunov.springdemo.dto.request.UserContactsInputDto;
import dev.svistunov.springdemo.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserContactsMapper {
    private final ModelMapper modelMapper;

    public UserContactsMapper() {
        this.modelMapper = new ModelMapper();
    }

    public UserContactsDto toDto(User user) {
        return modelMapper.map(user, UserContactsDto.class);
    }

    public User toEntity(UserContactsInputDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
