package dev.svistunov.springdemo.controller.interfaces;

import dev.svistunov.springdemo.dto.request.UserDetailsInputDto;
import dev.svistunov.springdemo.dto.request.UserDetailsSearchDto;
import dev.svistunov.springdemo.dto.response.UserDetailsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

public interface UserDetailsActions {
    @GetMapping
    Page<UserDetailsDto> index(@ModelAttribute UserDetailsSearchDto searchDto, Pageable pageable);

    @GetMapping("/{id}")
    UserDetailsDto show(@PathVariable Long id);

    @PostMapping
    UserDetailsDto create(@RequestBody UserDetailsInputDto user);

    @PutMapping("/{id}")
    UserDetailsDto update(@PathVariable Long id, @RequestBody UserDetailsInputDto user);

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id);
}
