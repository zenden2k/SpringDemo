package dev.svistunov.springdemo.controller;

import dev.svistunov.springdemo.controller.interfaces.UserContactsActions;
import dev.svistunov.springdemo.dto.request.UserContactsSearchDto;
import dev.svistunov.springdemo.dto.response.UserContactsDto;
import dev.svistunov.springdemo.dto.request.UserContactsInputDto;
import dev.svistunov.springdemo.services.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user_contacts")
public class UserContactsController implements UserContactsActions {
    private final UserService userService;

    public UserContactsController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping
    public Page<UserContactsDto> index(@ModelAttribute UserContactsSearchDto searchDto,
                                @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
                                Pageable pageable
    ) {
        return userService.searchUserDetails(searchDto, pageable);
    }

    @Override
    @GetMapping("/{id}")
    public UserContactsDto show(@PathVariable Long id) {
        return userService.getUserContactsById(id);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserContactsDto create(@RequestBody @Valid UserContactsInputDto userContactsDto) {
        return userService.createUserContact(userContactsDto);
    }

    @Override
    @PutMapping("/{id}")
    public UserContactsDto update(@PathVariable Long id, @RequestBody @Valid UserContactsInputDto userContactsDto) {
        return userService.updateContacts(id, userContactsDto);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
