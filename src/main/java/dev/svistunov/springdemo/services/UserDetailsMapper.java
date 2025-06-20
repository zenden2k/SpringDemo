package dev.svistunov.springdemo.services;

import dev.svistunov.springdemo.dto.response.UserDetailsDto;
import dev.svistunov.springdemo.dto.request.UserDetailsInputDto;
import dev.svistunov.springdemo.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsMapper {
    private final ModelMapper modelMapper;
    private final PhotoService photoService;

    public UserDetailsMapper(PhotoService photoService) {
        this.modelMapper = new ModelMapper();
        this.photoService = photoService;
    }

    public UserDetailsDto toDto(User user) {
        UserDetailsDto dto = modelMapper.map(user, UserDetailsDto.class);
        dto.setPhotoUrl(photoService.getUserPhotoUrl(user.getPhoto()));
        return dto;
    }

    public User toEntity(UserDetailsInputDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
