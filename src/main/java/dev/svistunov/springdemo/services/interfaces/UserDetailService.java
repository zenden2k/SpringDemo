package dev.svistunov.springdemo.services.interfaces;

import dev.svistunov.springdemo.dto.request.UserDetailsInputDto;
import dev.svistunov.springdemo.dto.request.UserDetailsSearchDto;
import dev.svistunov.springdemo.dto.response.UserDetailsDto;
import dev.svistunov.springdemo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserDetailService {
    List<UserDetailsDto> getAllUserDetails();

    User getById(Long id);

    Page<UserDetailsDto> searchUserDetails(UserDetailsSearchDto searchDto, Pageable pageable);

    UserDetailsDto createUserDetails(UserDetailsInputDto userDetailsDto);

    UserDetailsDto getUserDetailsById(Long id);

    UserDetailsDto updateUserDetails(Long id, UserDetailsInputDto userDetailsDto);

    void deleteUserDetails(Long id);
}
