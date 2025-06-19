package dev.svistunov.springdemo.controller;

import dev.svistunov.springdemo.dto.request.UserDetailsSearchDto;
import dev.svistunov.springdemo.dto.response.UserDetailsDto;
import dev.svistunov.springdemo.dto.request.UserDetailsInputDto;
import dev.svistunov.springdemo.services.UserDetailService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserDetailsController {
    private final UserDetailService userService;

    public UserDetailsController(UserDetailService userService) {
        this.userService = userService;
    }

    @GetMapping
    Page<UserDetailsDto> index(@ModelAttribute UserDetailsSearchDto searchDto,
                               @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return userService.searchUserDetails(searchDto, pageable);
    }

    @GetMapping("/{id}")
    UserDetailsDto show(@PathVariable Long id) {
        return userService.getUserDetailsById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDetailsDto create(@RequestBody @Valid UserDetailsInputDto user) {
        return userService.createUserDetails(user);
    }

    @PutMapping("/{id}")
    UserDetailsDto update(@PathVariable Long id, @RequestBody @Valid UserDetailsInputDto user) {
        return userService.updateUserDetails(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    void delete(@PathVariable Long id) {
        userService.deleteUserDetails(id);
    }
}
