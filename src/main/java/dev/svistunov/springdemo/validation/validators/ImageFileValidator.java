package dev.svistunov.springdemo.validation.validators;

import dev.svistunov.springdemo.validation.annotations.ValidImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import net.sf.jmimemagic.*;

import java.io.IOException;
import java.io.InputStream;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    private static final long MAX_FILE_SIZE = 1048576L;
    private static final Logger log = LoggerFactory.getLogger(ImageFileValidator.class);

    @Override
    public void initialize(ValidImage constraintAnnotation) {

    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        boolean result = true;

        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                        String.format("Размер файла не может быть больше чем %d", MAX_FILE_SIZE)
                    )
                    .addConstraintViolation();
            return false;
        }
        String contentType = /*multipartFile.getContentType()*/null;

        try {
            InputStream is = multipartFile.getInputStream();
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

        if (contentType == null || !isSupportedContentType(contentType)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Разрешены только JPEG и PNG файлы.")
                    .addConstraintViolation();

            result = false;
        }



        return result;
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }
}