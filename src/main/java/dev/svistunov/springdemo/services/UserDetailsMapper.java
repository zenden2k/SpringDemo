package dev.svistunov.springdemo.services;

import dev.svistunov.springdemo.dto.response.UserDetailsDto;
import dev.svistunov.springdemo.dto.request.UserDetailsInputDto;
import dev.svistunov.springdemo.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsMapper {
    private final ModelMapper modelMapper;

    public UserDetailsMapper() {
        this.modelMapper = new ModelMapper();
    }

    public UserDetailsDto toDto(User user) {
        return modelMapper.map(user, UserDetailsDto.class);
    }

    public User toEntity(UserDetailsInputDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
