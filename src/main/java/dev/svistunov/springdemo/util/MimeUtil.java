package dev.svistunov.springdemo.util;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MimeUtil {
    private static final Map<String, String> MIME_TO_EXTENSION = new HashMap<>();

    static {
        MIME_TO_EXTENSION.put("image/jpeg", "jpg");
        MIME_TO_EXTENSION.put("image/jpg", "jpg");
        MIME_TO_EXTENSION.put("image/png", "png");
    }

    public static String getExtensionByMimeType(String mimeType) {
        return MIME_TO_EXTENSION.getOrDefault(mimeType.toLowerCase(), "bin");
    }

    public static Path changeExtension(Path path, String newExtension) {
        String filename = path.getFileName().toString();
        String newFilename = changeExtension(filename, newExtension);
        return path.resolveSibling(newFilename);
    }

    private static String changeExtension(String filename, String newExtension) {
        newExtension = newExtension.startsWith(".") ? newExtension : "." + newExtension;

        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            return filename.substring(0, dotIndex) + newExtension;
        }
        return filename + newExtension;
    }
}
