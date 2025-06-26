package dev.svistunov.springdemo.controller.interfaces;

import dev.svistunov.springdemo.dto.response.UserPhotoDto;
import dev.svistunov.springdemo.validation.annotations.ValidImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
public interface UserPhotoActions {
    @GetMapping
    Page<UserPhotoDto> index(Pageable pageable);

    @GetMapping("/{id}")
    UserPhotoDto read(@PathVariable Long id);

    @PostMapping("/{id}")
    ResponseEntity<UserPhotoDto> upload(@PathVariable Long id, @RequestParam("file")  @ValidImage MultipartFile file);

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}") void delete(@PathVariable Long id);
}
