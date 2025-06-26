package dev.svistunov.springdemo.services;

import dev.svistunov.springdemo.dto.response.UserDetailsDto;
import dev.svistunov.springdemo.dto.request.UserDetailsInputDto;
import dev.svistunov.springdemo.model.User;
import dev.svistunov.springdemo.services.interfaces.PhotoService;
import dev.svistunov.springdemo.services.interfaces.UserDetailsMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsMapperImpl implements UserDetailsMapper {
    private final ModelMapper modelMapper;
    private final PhotoService photoService;

    public UserDetailsMapperImpl(PhotoService photoService) {
        this.modelMapper = new ModelMapper();
        this.photoService = photoService;
    }

    @Override
    public UserDetailsDto toDto(User user) {
        UserDetailsDto dto = modelMapper.map(user, UserDetailsDto.class);
        dto.setPhotoUrl(photoService.getUserPhotoUrl(user.getPhoto()));
        return dto;
    }

    @Override
    public User toEntity(UserDetailsInputDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
