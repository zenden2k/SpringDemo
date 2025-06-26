package dev.svistunov.springdemo.services;

import dev.svistunov.springdemo.dto.response.UserPhotoDto;
import dev.svistunov.springdemo.exception.FileValidationException;
import dev.svistunov.springdemo.model.User;
import dev.svistunov.springdemo.repository.UserRepository;
import dev.svistunov.springdemo.services.interfaces.PhotoService;
import dev.svistunov.springdemo.services.interfaces.UserService;
import dev.svistunov.springdemo.util.MimeUtil;
import dev.svistunov.springdemo.validation.annotations.ValidImage;
import dev.svistunov.springdemo.validation.validators.ImageFileValidator;
import net.sf.jmimemagic.*;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PhotoServiceImpl implements PhotoService {
    private static final Logger log = LoggerFactory.getLogger(PhotoServiceImpl.class);
    public static final String UPLOAD_DIR = "uploads/";
    private final Environment env;
    private final UserService userService;
    private final UserRepository userRepository;

    public PhotoServiceImpl(Environment env, UserService userService, UserRepository userRepository) {
        this.env = env;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void deletePhoto(String fileName) {
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Ошибка при удалении файла: {}", e.getMessage());
        }
    }

    @Override
    public String getAppUrl() throws UnknownHostException {
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        return "http://" + InetAddress.getLocalHost().getHostName() +
                (port.equals("80") ? "" : ":" + port) +
                contextPath;
    }

    @Override
    public String getUserPhotoUrl(String filename) {
        if (!StringUtils.hasText(filename)) {
            return null;
        }
        String host = "http://localhost:8080";
        try {
            host = getAppUrl();
        } catch (Exception e) {
            log.warn("Exception: {}", e.getMessage());
        }
        return host + "/" + UPLOAD_DIR + filename;
    }

    @Override
    public UserPhotoDto uploadPhoto(Long id, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        Path targetPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath);
        }

        // Генерируем уникальное имя файла
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = targetPath.resolve(filename);

        Files.copy(file.getInputStream(), filePath);

        File initialFile = new File(filePath.toUri());
        String contentType = "application/octet-stream";

        try (InputStream fileStream = new FileInputStream(initialFile)) {
            MagicMatch match = Magic.getMagicMatch(fileStream.readNBytes(100), false);
            contentType = match.getMimeType();
        } catch (IOException e) {
            log.error("Ошибка при чтении файла {}", e.getMessage());
        } catch (MagicException | MagicParseException | MagicMatchNotFoundException e) {
            log.error("Ошибка при определении Content Type: {}", e.getMessage());
        }

        if (!ImageFileValidator.isSupportedContentType(contentType)) {
            throw new FileValidationException("Разрешены только JPEG и PNG файлы.");
        }
        // Переименовываем файл согласно его mime type
        String ext = MimeUtil.getExtensionByMimeType(contentType);
        Path newPath = MimeUtil.changeExtension(filePath, ext);

        Files.move(filePath, newPath);

        return updateUserPhoto(id, newPath.getFileName().toString());
    }

    // Работа с фото пользователя
    @Override
    public Page<UserPhotoDto> getAllUserPhotos(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map((user) -> new UserPhotoDto(user.getId(),
                        getUserPhotoUrl(user.getPhoto()))
                );
    }

    @Override
    public UserPhotoDto getUserPhotoUrl(Long id) {
        User user = userService.getById(id);
        String photo = user.getPhoto();
        return new UserPhotoDto(id, getUserPhotoUrl(photo));
    }

    @Override
    public UserPhotoDto updateUserPhoto(Long id, String filename) {
        User user = userService.getById(id);
        String photo = user.getPhoto();

        // Удаляем файл старого фото из хранилища (с диска)
        if (StringUtils.hasText(photo)) {
            deletePhoto(photo);
        }
        user.setPhoto(filename);
        userRepository.save(user);
        return new UserPhotoDto(id, getUserPhotoUrl(filename));
    }

    @Override
    public void deleteUserPhoto(Long id) {
        User user = userService.getById(id);
        String photo = user.getPhoto();

        // Удаляем файл из хранилища
        if (StringUtils.hasText(photo)) {
            deletePhoto(photo);
        }
        user.setPhoto(null);
        userRepository.save(user);
    }
}
