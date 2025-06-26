package dev.svistunov.springdemo.services.interfaces;

import dev.svistunov.springdemo.dto.response.UserPhotoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.UnknownHostException;

public interface PhotoService {

    void deletePhoto(String fileName);

    String getAppUrl() throws UnknownHostException;

    String getUserPhotoUrl(String filename);

    UserPhotoDto uploadPhoto(Long id, @RequestParam("file") MultipartFile file) throws IOException;

    Page<UserPhotoDto> getAllUserPhotos(Pageable pageable);

    UserPhotoDto getUserPhotoUrl(Long id);

    UserPhotoDto updateUserPhoto(Long id, String filename);

    void deleteUserPhoto(Long id);
}
