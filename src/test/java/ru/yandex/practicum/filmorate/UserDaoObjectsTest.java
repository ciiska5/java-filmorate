package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserDaoObjectsTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private User createTestUser() {
        Set<Integer> friends = new HashSet<>();

        return new User(
                1,
                "mail@mail.ru",
                "testLogin",
                "testName",
                LocalDate.parse("1946-08-20"),
                friends
        );
    }

    @SneakyThrows
    @DisplayName("Должен создавать пользователя")
    @Test
    void shouldCreateValidUser() {
        User thisTestUser = createTestUser();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestUser)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @DisplayName("Не должен создавать пользователя с пробелом в логине")
    @Test
    void shouldNotCreateUserWithTwoWordsLogin() {
        User thisTestUser = createTestUser();

        thisTestUser.setLogin("dolore ullamco");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestUser)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @DisplayName("Не должен создавать пользователя с неправильной почтой")
    @Test
    void shouldNotCreateUserWithIncorrectEmail() {
        User thisTestUser = createTestUser();

        thisTestUser.setEmail("mail.ru");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestUser)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @DisplayName("Не должен создавать пользователя с ненаступившей датой рождения")
    @Test
    void shouldNotCreateUserWithFutureBirthdate() {
        User thisTestUser = createTestUser();

        thisTestUser.setBirthday(LocalDate.parse("2446-08-20"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestUser)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @DisplayName("Должен создавать пользователя с неуказанным именем при указании логина")
    @Test
    void shouldCreateUserWithEmptyName() {
        User thisTestUser = createTestUser();

        thisTestUser.setName("");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestUser)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @DisplayName("Должен обновлять пользователя с правильно указанным id")
    @Test
    void shouldUpdateUserWithCorrectId() {
        User thisTestUser = createTestUser();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestUser)))
                .andExpect(status().is(200));

        thisTestUser = new User(
                1,
                "mail@yandex.ru",
                "doloreUpdate",
                "est adipisicing",
                LocalDate.parse("1976-09-20"),
                thisTestUser.getFriends()
        );

        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestUser)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @DisplayName("Не должен обновлять пользователя с неправильно указанным id")
    @Test
    void shouldNotUpdateUserWithIncorrectId() {
        User thisTestUser = createTestUser();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestUser)))
                .andExpect(status().is(200));

        thisTestUser = new User(
                9999,
                "mail@yandex.ru",
                "doloreUpdate",
                "est adipisicing",
                LocalDate.parse("1976-09-20"),
                thisTestUser.getFriends());

        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestUser)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @DisplayName("Должен вернуть всех пользователей")
    @Test
    void shouldReturnUsersList() {
        User thisTestUser = createTestUser();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestUser)))
                .andExpect(status().is(200));

        mockMvc.perform(get("/users"))
                .andExpect(status().is(200));
    }
}
