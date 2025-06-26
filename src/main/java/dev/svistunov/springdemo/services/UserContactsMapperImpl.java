package dev.svistunov.springdemo.services;

import dev.svistunov.springdemo.dto.response.UserContactsDto;
import dev.svistunov.springdemo.dto.request.UserContactsInputDto;
import dev.svistunov.springdemo.model.User;
import dev.svistunov.springdemo.services.interfaces.UserContactsMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserContactsMapperImpl implements UserContactsMapper {
    private final ModelMapper modelMapper;

    public UserContactsMapperImpl() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public UserContactsDto toDto(User user) {
        return modelMapper.map(user, UserContactsDto.class);
    }

    @Override
    public User toEntity(UserContactsInputDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
