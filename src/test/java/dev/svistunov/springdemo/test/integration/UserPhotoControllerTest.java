package dev.svistunov.springdemo.test.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.svistunov.springdemo.model.User;
import dev.svistunov.springdemo.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class UserPhotoControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private WebApplicationContext context;

    private static final byte[] PNG_SIGNATURE = {
            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
            (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A
    };

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        createTestUser("Иван", "ivan@gmail.com", "89218883322", "ivan.jpg");
        createTestUser("Михаил", "mihail@ya.ru", "+79115523458", "mihail.png");
        createTestUser("Иннокентий", "kesha@yahoo.com", "+78125523458", null);
        createTestUser("Анатолий", "tolik@mail.ru", "+79126179287", null);
        createTestUser("Пётр", "petr@mail.ru", "+79653280032", "petr_square.jpg");
    }

    @AfterEach
    void shutdown() {
        userRepository.deleteAll();
        // Сбрасываем счетчик
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1").executeUpdate();
    }

    @Test
    void userPhotos_whenGet_thenStatus200Returned() throws Exception {
        mockMvc.perform(get("/api/user_photo")
                        .header("Authorization", getAuthorization()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5)
        );
    }
    
    @Test
    public void  givenId_whenRead_thenStatus200andUserReturned() throws Exception {
        mockMvc.perform(get("/api/user_photo/5")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.photoUrl").value(startsWith("http")));
    }

    @Test
    public void givenId_whenReadEmptyPhoto_thenStatus200andUserReturned() throws Exception {
        mockMvc.perform(get("/api/user_photo/3")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.photoUrl").value(Matchers.nullValue()));
    }

    @Test
    public void givenId_whenGetNotExistingUser_thenStatus404() throws Exception {
        mockMvc.perform(get("/user_photo/1546546")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenId_whenUploadPhoto_thenStatus200andUserReturned() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("linux.jpg")) {
            Assertions.assertNotNull(is);
            byte[] bytes = is.readAllBytes();

            MockMultipartFile firstFile = new MockMultipartFile("file", "filename.jpg", "image/jpg", bytes);

            mockMvc.perform(multipart("/api/user_photo/1").file(firstFile)
                            .header("Authorization", getAuthorization())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.photoUrl").value(startsWith("http")));
        }
    }

    @Test
    public void  givenId_whenUploadBadPhoto_thenStatus200andUserReturned() throws Exception {
        MockMultipartFile firstFile = new MockMultipartFile("file", "filename.json", "applcation/json", "{}".getBytes());

        mockMvc.perform(multipart("/api/user_photo/1").file(firstFile)
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(1))
                .andExpect(jsonPath("$.errors.file").exists());
    }

    @Test
    public void givenId_whenUpdateNotExistingUser_thenStatus404Returned() throws Exception {
        MockMultipartFile firstFile = new MockMultipartFile("file", "filename.png", "image/png", PNG_SIGNATURE);

        mockMvc.perform(multipart("/api/user_photo/13425345").file(firstFile)
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenId_whenDelete_thenStatus200Returned() throws Exception {
        mockMvc.perform(delete("/api/user_photo/1")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isOk());
    }

    @Test
    public void givenId_whenDeleteNotExistingUser_thenStatus404() throws Exception {
        mockMvc.perform(delete("/user_photo/1546546")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    private void createTestUser(String firstName, String email, String phoneNumber, String photo) {
        User user = new User();
        user.setFirstName(firstName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPhoto(photo);
        userRepository.save(user);
    }

    private String getAuthorization() {
        final String auth = "admin:admin";
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }
}
