package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Film film = new Film();
    private User user = new User();

    @AfterEach
    void createNewObject() {
        film = new Film();
        user = new User();
    }


    @SneakyThrows
    @Test
    void shouldCreateValidUser() {
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.parse("1946-08-20"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    void shouldNotCreateUserWithTwoWordsLogin() {
        user.setLogin("dolore ullamco");
        user.setEmail("yandex@mail.ru");
        user.setBirthday(LocalDate.parse("1995-08-20"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(500));
    }

    @SneakyThrows
    @Test
    void shouldNotCreateUserWithIncorrectEmail() {
        user.setLogin("dolore");
        user.setEmail("mail.ru");
        user.setBirthday(LocalDate.parse("1980-08-20"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(500));
    }

    @SneakyThrows
    @Test
    void shouldNotCreateUserWithFutureBirthdate() {
        user.setLogin("dolore");
        user.setEmail("test@mail.ru");
        user.setBirthday(LocalDate.parse("2446-08-20"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(500));
    }

    @SneakyThrows
    @Test
    void shouldUpdateUserWithCorrectId() {
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.parse("1946-08-20"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(200));

        user = new User(
                1,
                "mail@yandex.ru",
                "doloreUpdate",
                "est adipisicing",
                LocalDate.parse("1976-09-20"), user.getFriends());

        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    void shouldNotUpdateUserWithiIncorrectId() {
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.parse("1946-08-20"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(200));

        user = new User(
                9999,
                "mail@yandex.ru",
                "doloreUpdate",
                "est adipisicing",
                LocalDate.parse("1976-09-20"), user.getFriends());

        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void shouldReturnUsersList() {
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.parse("1946-08-20"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(200));

        mockMvc.perform(get("/users"))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    void shouldCreateUserWithEmptyName() {
        user.setLogin("common");
        user.setEmail("friend@common.ru");
        user.setBirthday(LocalDate.parse("2000-08-20"));

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(200));
    }


    @SneakyThrows
    @Test
    void shouldCreateFilm() {
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(100L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    void shouldNotCreateFilmWithEmptyName() {
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.parse("1900-03-25"));
        film.setDuration(200L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(500));
    }

    @SneakyThrows
    @Test
    void shouldNotCreateFilmWithDescriptionAbove200Characters() {
        film.setName("Film name");
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                "а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.");
        film.setReleaseDate(LocalDate.parse("1900-03-25"));
        film.setDuration(200L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(500));
    }

    @SneakyThrows
    @Test
    void shouldNotCreateFilmWithReleaseDateBefore1895_12_28() {
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.parse("1890-03-25"));
        film.setDuration(200L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void shouldNotCreateFilmWithIncorrectDuration() {
        film.setName("Name");
        film.setDescription("Descrition");
        film.setReleaseDate(LocalDate.parse("1980-03-25"));
        film.setDuration(-100L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(500));
    }

    @SneakyThrows
    @Test
    void shouldUpdateFilmWithCorrectId() {
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(100L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(200));

        film = new Film(
                1,
                "Film Updated",
                "New film update decription",
                LocalDate.parse("1989-04-17"),
                190L,
                film.getLikes()
        );

        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    void shouldNotUpdateFilmWithIncorrectId() {
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(100L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(200));

        film = new Film(
                9999,
                "Film Updated",
                "New film update decription",
                LocalDate.parse("1989-04-17"),
                190L,
                film.getLikes()
        );

        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void shouldReturnFilmsList() {
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(100L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(200));

        mockMvc.perform(get("/films"))
                .andExpect(status().is(200));
    }
}
