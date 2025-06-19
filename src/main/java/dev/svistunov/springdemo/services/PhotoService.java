package dev.svistunov.springdemo.services;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PhotoService {
    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);
    public static final String UPLOAD_DIR = "uploads/";
    private final Environment env;

    public PhotoService(Environment env) {
        this.env = env;

    }
    void deletePhoto(String fileName) {
        Path filePath = Paths.get(UPLOAD_DIR + fileName);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Ошибка при удалении файла: {}", e.getMessage());
        }
    }

    public String getAppUrl() throws UnknownHostException {
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        return "http://" + InetAddress.getLocalHost().getHostName() +
                (port.equals("80") ? "" : ":" + port) +
                contextPath;
    }

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
}
