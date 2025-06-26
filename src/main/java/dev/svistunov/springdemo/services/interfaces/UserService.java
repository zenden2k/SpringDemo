package dev.svistunov.springdemo.services.interfaces;

import dev.svistunov.springdemo.dto.request.UserContactsInputDto;
import dev.svistunov.springdemo.dto.request.UserContactsSearchDto;
import dev.svistunov.springdemo.dto.response.UserContactsDto;
import dev.svistunov.springdemo.dto.response.UserPhotoDto;
import dev.svistunov.springdemo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User save(User user);

    List<User> getAll();

    User getById(Long id);

    User create(User user);

    UserContactsDto createUserContact(UserContactsInputDto userDto);

    User update(Long id, User user);

    void delete(Long id);

    List<UserContactsDto> getAllContacts();

    Page<UserContactsDto> searchUserDetails(UserContactsSearchDto searchDto, Pageable pageable);

    UserContactsDto getUserContactsById(Long id);

    UserContactsDto updateContacts(Long id, UserContactsInputDto userContactsDto);
}
