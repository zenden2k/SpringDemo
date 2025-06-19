package dev.svistunov.springdemo.controller;

import dev.svistunov.springdemo.dto.request.UserContactsSearchDto;
import dev.svistunov.springdemo.dto.response.UserContactsDto;
import dev.svistunov.springdemo.dto.request.UserContactsInputDto;
import dev.svistunov.springdemo.services.UserContactsMapper;
import dev.svistunov.springdemo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user_contacts")
public class UserContactsController {
    private final UserService userService;

    public UserContactsController(UserService userService, UserContactsMapper contactsModelMapper) {
        this.userService = userService;
    }

    @GetMapping
    Page<UserContactsDto> index(@ModelAttribute UserContactsSearchDto searchDto,
                                @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
                                Pageable pageable
    ) {
        return userService.searchUserDetails(searchDto, pageable);
    }

    @GetMapping("/{id}")
    UserContactsDto show(@PathVariable Long id) {
        return userService.getUserContactsById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserContactsDto create(@RequestBody @Valid UserContactsInputDto userContactsDto) {
        return userService.createUserContact(userContactsDto);
    }

    @PutMapping("/{id}")
    UserContactsDto update(@PathVariable Long id, @RequestBody @Valid UserContactsInputDto userContactsDto) {
        return userService.updateContacts(id, userContactsDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
