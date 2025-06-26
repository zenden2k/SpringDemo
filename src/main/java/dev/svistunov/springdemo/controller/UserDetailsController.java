package dev.svistunov.springdemo.controller;

import dev.svistunov.springdemo.controller.interfaces.UserDetailsActions;
import dev.svistunov.springdemo.dto.request.UserDetailsInputDto;
import dev.svistunov.springdemo.dto.request.UserDetailsSearchDto;
import dev.svistunov.springdemo.dto.response.UserDetailsDto;
import dev.svistunov.springdemo.services.interfaces.UserDetailService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserDetailsController implements UserDetailsActions {
    private final UserDetailService userService;

    public UserDetailsController(UserDetailService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping
    public Page<UserDetailsDto> index(@ModelAttribute UserDetailsSearchDto searchDto,
                               @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return userService.searchUserDetails(searchDto, pageable);
    }

    @Override
    @GetMapping("/{id}")
    public UserDetailsDto show(@PathVariable Long id) {
        return userService.getUserDetailsById(id);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsDto create(@RequestBody @Valid UserDetailsInputDto user) {
        return userService.createUserDetails(user);
    }

    @Override
    @PutMapping("/{id}")
    public UserDetailsDto update(@PathVariable Long id, @RequestBody @Valid UserDetailsInputDto user) {
        return userService.updateUserDetails(id, user);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long id) {
        userService.deleteUserDetails(id);
    }
}
