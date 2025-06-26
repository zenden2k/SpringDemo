package dev.svistunov.springdemo.controller;

import dev.svistunov.springdemo.controller.interfaces.UserPhotoActions;
import dev.svistunov.springdemo.dto.response.UserPhotoDto;
import dev.svistunov.springdemo.services.interfaces.PhotoService;
import dev.svistunov.springdemo.validation.annotations.ValidImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Validated
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS) // 404 error without it, see https://stackoverflow.com/questions/56613618/spring-5-pathvariable-validation-causing-http-404
@RequestMapping("/api/user_photo")
public class UserPhotoController implements UserPhotoActions {
    private final PhotoService photoService;
    private static final Logger log = LoggerFactory.getLogger(UserPhotoController.class);

    public UserPhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @Override
    @GetMapping
    public Page<UserPhotoDto> index(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
        Pageable pageable
    ) {
        return photoService.getAllUserPhotos(pageable);
    }

    @Override
    @GetMapping("/{id}")
    public UserPhotoDto read(@PathVariable Long id) {
        return photoService.getUserPhotoUrl(id);
    }

    @Override
    @PostMapping("/{id}")
    public ResponseEntity<UserPhotoDto> upload(@PathVariable Long id, @RequestParam("file") @ValidImage MultipartFile file) {
        try {
            return ResponseEntity.ok(photoService.uploadPhoto(id, file));
        } catch (IOException e) {
            log.error("Ошибка при загрузке фото: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        photoService.deleteUserPhoto(id);
    }
}
