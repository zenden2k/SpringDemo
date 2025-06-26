package dev.svistunov.springdemo.controller.interfaces;

import dev.svistunov.springdemo.dto.request.UserContactsSearchDto;
import dev.svistunov.springdemo.dto.response.UserContactsDto;
import dev.svistunov.springdemo.dto.request.UserContactsInputDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

public interface UserContactsActions {
    @GetMapping
    Page<UserContactsDto> index(@ModelAttribute UserContactsSearchDto searchDto, Pageable pageable);

    @GetMapping("/{id}")
    UserContactsDto show(@PathVariable Long id);

    @PostMapping
    UserContactsDto create(@RequestBody @Valid UserContactsInputDto userContactsDto);

    @PutMapping("/{id}")
    UserContactsDto update(@PathVariable Long id, @RequestBody @Valid UserContactsInputDto userContactsDto);

    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id);
}