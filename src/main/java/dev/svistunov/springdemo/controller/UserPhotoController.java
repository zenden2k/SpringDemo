package dev.svistunov.springdemo.controller;

import dev.svistunov.springdemo.dto.response.UserPhotoDto;

import dev.svistunov.springdemo.services.PhotoService;
import dev.svistunov.springdemo.services.UserService;
import dev.svistunov.springdemo.util.MimeUtil;
import dev.svistunov.springdemo.validation.annotations.ValidImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import net.sf.jmimemagic.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static java.nio.file.Paths.*;

@RestController
@Validated
@RequestMapping("/api/user_photo")
public class UserPhotoController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserPhotoController.class);

    public UserPhotoController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    Page<UserPhotoDto> index(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
        Pageable pageable
    ) {
        return userService.getAllUserPhotos(pageable);
    }

    @GetMapping("/{id}")
    UserPhotoDto read(@PathVariable Long id) {
        return userService.getUserPhotoUrl(id);
    }

    @PostMapping("/{id}")
    ResponseEntity<UserPhotoDto> upload(@PathVariable Long id, @RequestParam("file") @ValidImage MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            Path targetPath = get(PhotoService.UPLOAD_DIR);
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }

            // Генерируем уникальное имя файла
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = targetPath.resolve(filename);

            String contentType = "application/octet-stream";
            try {
                InputStream is = file.getInputStream();
                if (is.markSupported()) {
                    is.mark(100);

                    MagicMatch match = Magic.getMagicMatch(is.readNBytes(100), false);
                    contentType = match.getMimeType();

                    // Возвращаемся в начало
                    is.reset();
                }
            } catch (IOException e) {
                log.error("Ошибка при чтении файла {}", e.getMessage());
            } catch (MagicException | MagicParseException | MagicMatchNotFoundException e) {
                log.error("Ошибка при определении Content Type: {}", e.getMessage());
            }

            String ext = MimeUtil.getExtensionByMimeType(contentType);
            Path newPath = MimeUtil.changeExtension(filePath, ext);
            Files.copy(file.getInputStream(), newPath);

            return ResponseEntity.ok(userService.updateUserPhoto(id, filename));
        } catch (IOException e) {
            log.error("Ошибка при загрузке фото: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}") void delete(@PathVariable Long id) {
        userService.deleteUserPhoto(id);
    }
}
