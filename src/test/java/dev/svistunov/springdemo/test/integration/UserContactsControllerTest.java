package dev.svistunov.springdemo.test.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.svistunov.springdemo.dto.request.UserContactsInputDto;
import dev.svistunov.springdemo.exception.UserNotFoundException;
import dev.svistunov.springdemo.model.User;
import dev.svistunov.springdemo.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class UserContactsControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        createTestUser("Иван", "ivan@gmail.com", "89218883322");
        createTestUser("Михаил", "mihail@ya.ru", "+79115523458");
        createTestUser("Иннокентий", "kesha@yahoo.com", "+78125523458");
        createTestUser("Анатолий", "tolik@mail.ru", "+79126179287");
        createTestUser("Пётр", "petr@mail.ru", "+79653280032");
    }

    @AfterEach
    void shutdown() {
        userRepository.deleteAll();
        // Сбрасываем счетчик
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1").executeUpdate();
    }

    @Test
    void user_whenGet_thenStatus200Returned() throws Exception {
        // По-умолчанию идёт сортировка по id, поэтому можно быть спокойным за порядок
        mockMvc.perform(get("/api/user_contacts")
                        .header("Authorization", getAuthorization()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].firstName").value("Михаил"))
                .andExpect(jsonPath("$.content[1].email").value("mihail@ya.ru"))
                .andExpect(jsonPath("$.content[1].phoneNumber").value("+79115523458"));
    }

    @Test
    void user_whenGetFiltered_thenStatus200Returned() throws Exception {
        mockMvc.perform(get("/api/user_contacts")
                        .header("Authorization", getAuthorization())
                        .queryParam("firstName", "Михаил")
                        .queryParam("email", "mihail@ya.ru")
                       )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].firstName").value("Михаил"))
                .andExpect(jsonPath("$.content[0].email").value("mihail@ya.ru"))
                .andExpect(jsonPath("$.content[0].phoneNumber").value("+79115523458"));
    }

    @Test
    void user_whenGetFilteredByPhoneNumber_thenStatus200Returned() throws Exception {
        mockMvc.perform(get("/api/user_contacts")
                        .header("Authorization", getAuthorization())
                        .queryParam("phoneNumber", "8(965)328-00-32")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(5))
                .andExpect(jsonPath("$.content[0].firstName").value("Пётр"))
                .andExpect(jsonPath("$.content[0].email").value("petr@mail.ru"))
                .andExpect(jsonPath("$.content[0].phoneNumber").value("+79653280032"));
    }

    @Test
    public void users_whenRead_thenStatus200andUserReturned() throws Exception {
        mockMvc.perform(get("/api/user_contacts/4")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Анатолий"))
                .andExpect(jsonPath("$.email").value("tolik@mail.ru"))
                .andExpect(jsonPath("$.phoneNumber").value("+79126179287"));
    }

    @Test
    public void givenId_whenGetNotExistingUser_thenStatus404() throws Exception {
        mockMvc.perform(get("/user_contacts/1546546")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenUser_whenCreate_thenStatus201andUserReturned() throws Exception {
        UserContactsInputDto user = new UserContactsInputDto();
        user.setFirstName("Андрей");
        user.setEmail("test@gmail.com");
        user.setPhoneNumber("+79115653245");
        mockMvc.perform(post("/api/user_contacts")
                                .header("Authorization", getAuthorization())
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Андрей"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+79115653245"));

        assertEquals(6, userRepository.count());
    }

    @Test
    public void givenUser_whenCreateWithInvalidData_thenStatus400Returned() throws Exception {
        UserContactsInputDto user = new UserContactsInputDto();
        user.setFirstName("Ан<>др/?ей");
        user.setEmail("testgmail.com");
        user.setPhoneNumber("+7&&9115653245");
        mockMvc.perform(post("/api/user_contacts")
                        .header("Authorization", getAuthorization())
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(3))
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.phoneNumber").exists());
    }

    @Test
    public void givenUser_whenUpdate_thenStatus200andUserReturned() throws Exception {
        UserContactsInputDto user = new UserContactsInputDto();
        user.setFirstName("Андрей");
        user.setEmail("test2@gmail.com");
        user.setPhoneNumber("+79115653246");
        mockMvc.perform(put("/api/user_contacts/1")
                        .header("Authorization", getAuthorization())
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Андрей"))
                .andExpect(jsonPath("$.email").value("test2@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+79115653246"));
    }

    @Test
    public void givenUser_whenUpdateNotExistingUser_thenStatus404Returned() throws Exception {
        UserContactsInputDto user = new UserContactsInputDto();
        user.setFirstName("Андрей");
        user.setEmail("test2@gmail.com");
        user.setPhoneNumber("+79115653246");
        mockMvc.perform(put("/api/user_contacts/1345345")
                        .header("Authorization", getAuthorization())
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(UserNotFoundException.class, result.getResolvedException()));;
    }

    @Test
    public void givenId_whenDelete_thenStatus200Returned() throws Exception {
        mockMvc.perform(delete("/api/user_contacts/1")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isOk());
        assertEquals(4, userRepository.count());
    }

    @Test
    public void givenId_whenDeleteNotExistingUser_thenStatus404() throws Exception {
        mockMvc.perform(delete("/user_contacts/1546546")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    private void createTestUser(String firstName, String email, String phoneNumber) {
        User user = new User();
        user.setFirstName(firstName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);
    }

    private String getAuthorization() {
        final String auth = "admin:admin";
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }
}
