package dev.svistunov.springdemo.test.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.svistunov.springdemo.dto.request.UserDetailsInputDto;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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
public class UserDetailsControllerTest {
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

        createTestUser("Иван",  "Сидоров", "Петрович","ivan@gmail.com", "89218883322", "1983-12-02");
        createTestUser("Михаил",  "Шаляпин", "Юрьевич", "mihail@ya.ru", "+79115523458", "1975-05-07");
        createTestUser("Иннокентий", "Смоктуновский", "Михайлович","kesha@yahoo.com", "+78125523458", "1925-03-28");
        createTestUser("Анатолий", "Вассерман", "Егорович", "tolik@mail.ru", "+79126179287", "1960-09-15");
        createTestUser("Пётр", "Талалихин", "Иванович", "petr@mail.ru", "+79653280032", "2002-01-20");
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
        mockMvc.perform(get("/api/users")
                        .header("Authorization", getAuthorization()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].firstName").value("Михаил"))
                .andExpect(jsonPath("$.content[1].lastName").value("Шаляпин"))
                .andExpect(jsonPath("$.content[1].middleName").value("Юрьевич"))
                .andExpect(jsonPath("$.content[1].email").value("mihail@ya.ru"))
                .andExpect(jsonPath("$.content[1].phoneNumber").value("+79115523458"))
                .andExpect(jsonPath("$.content[1].birthDate").value("1975-05-07")
                );
    }

    @Test
    void user_whenGetFiltered_thenStatus200Returned() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", getAuthorization())
                        .queryParam("firstName", "Михаил")
                        .queryParam("email", "mihail@ya.ru")
                       )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].firstName").value("Михаил"))
                .andExpect(jsonPath("$.content[0].email").value("mihail@ya.ru"))
                .andExpect(jsonPath("$.content[0].phoneNumber").value("+79115523458"))
                .andExpect(jsonPath("$.content[0].birthDate").value("1975-05-07")
                );
    }

    @Test
    void user_whenGetFilteredByDate_thenStatus200Returned() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/users")
                        .header("Authorization", getAuthorization())
                        .queryParam("birthDateFrom", "1920-01-01")
                        .queryParam("birthDateTo", "1970-01-01")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(3))
                .andExpect(jsonPath("$.content[0].firstName").value("Иннокентий"))
                .andExpect(jsonPath("$.content[0].lastName").value("Смоктуновский"))
                .andExpect(jsonPath("$.content[0].birthDate").value("1925-03-28"))

                .andExpect(jsonPath("$.content[1].id").value(4))
                .andExpect(jsonPath("$.content[1].firstName").value("Анатолий"))
                .andExpect(jsonPath("$.content[1].lastName").value("Вассерман"))
                .andExpect(jsonPath("$.content[1].middleName").value("Егорович"))
                .andExpect(jsonPath("$.content[1].email").value("tolik@mail.ru"))
                .andExpect(jsonPath("$.content[1].phoneNumber").value("+79126179287"))
                .andExpect(jsonPath("$.content[1].birthDate").value("1960-09-15")
                );
    }

        @Test
    public void users_whenRead_thenStatus200andUserReturned() throws Exception {
        mockMvc.perform(get("/api/users/4")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Анатолий"))
                .andExpect(jsonPath("$.lastName").value("Вассерман"))
                .andExpect(jsonPath("$.middleName").value("Егорович"))
                .andExpect(jsonPath("$.email").value("tolik@mail.ru"))
                .andExpect(jsonPath("$.phoneNumber").value("+79126179287"))
                .andExpect(jsonPath("$.birthDate").value("1960-09-15")
                );
    }

    @Test
    public void givenId_whenGetNotExistingUser_thenStatus404() throws Exception {
        mockMvc.perform(get("/users/1546546")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenUser_whenCreate_thenStatus201andUserReturned() throws Exception {
        UserDetailsInputDto user = new UserDetailsInputDto();
        user.setFirstName("Андрей");
        user.setLastName("Петров");
        user.setMiddleName("Иванович");
        user.setEmail("test@gmail.com");
        user.setPhoneNumber("+79115653245");
        user.setBirthDate(LocalDate.parse("1993-12-13"));
        mockMvc.perform(post("/api/users")
                                .header("Authorization", getAuthorization())
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Андрей"))
                .andExpect(jsonPath("$.lastName").value("Петров"))
                .andExpect(jsonPath("$.middleName").value("Иванович"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+79115653245"))
                .andExpect(jsonPath("$.birthDate").value("1993-12-13"));

        assertEquals(6, userRepository.count());
    }

    @Test
    public void givenUser_whenCreateWithInvalidData_thenStatus400Returned() throws Exception {
        UserDetailsInputDto user = new UserDetailsInputDto();
        user.setFirstName("Ан<>др/?ей");
        user.setLastName("^%*&*^%*^&");
        user.setMiddleName("^^&)*(&");
        user.setEmail("testgmail.com");
        user.setPhoneNumber("+7&&9115653245");
        user.setBirthDate(LocalDate.parse("2040-01-01"));
        mockMvc.perform(post("/api/users")
                        .header("Authorization", getAuthorization())
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(6))
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.lastName").exists())
                .andExpect(jsonPath("$.errors.middleName").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.phoneNumber").exists())
                .andExpect(jsonPath("$.errors.birthDate").exists());
    }

    @Test
    public void givenUser_whenUpdate_thenStatus200andUserReturned() throws Exception {
        UserDetailsInputDto user = new UserDetailsInputDto();
        user.setFirstName("Андрей");
        user.setLastName("Петров");
        user.setMiddleName("Иванович");
        user.setEmail("test@gmail.com");
        user.setPhoneNumber("+79115653245");
        user.setBirthDate(LocalDate.parse("1993-12-13"));
        mockMvc.perform(put("/api/users/1")
                        .header("Authorization", getAuthorization())
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Андрей"))
                .andExpect(jsonPath("$.lastName").value("Петров"))
                .andExpect(jsonPath("$.middleName").value("Иванович"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+79115653245"))
                .andExpect(jsonPath("$.birthDate").value("1993-12-13"));
        ;
    }

    @Test
    public void givenUser_whenUpdateNotExistingUser_thenStatus404Returned() throws Exception {
        UserDetailsInputDto user = new UserDetailsInputDto();
        user.setFirstName("Андрей");
        user.setEmail("test2@gmail.com");
        user.setPhoneNumber("+79115653246");
        mockMvc.perform(put("/api/users/1345345")
                        .header("Authorization", getAuthorization())
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(UserNotFoundException.class, result.getResolvedException()));;
    }

    @Test
    public void givenId_whenDelete_thenStatus200Returned() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isOk());
    }

    @Test
    public void givenId_whenDeleteNotExistingUser_thenStatus404() throws Exception {
        mockMvc.perform(delete("/users/1546546")
                        .header("Authorization", getAuthorization())
                )
                .andExpect(status().isNotFound());
    }

    private void createTestUser(String firstName, String lastName, String middleName, String email, String phoneNumber, String birthDate) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMiddleName(middleName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setBirthDate(LocalDate.parse(birthDate));
        userRepository.save(user);
    }

    private String getAuthorization() {
        final String auth = "admin:admin";
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }
}
