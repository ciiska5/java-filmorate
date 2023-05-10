package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    @NotNull
    @NotBlank
    @Email(message = "email не соответствует нужному формату")
    private String email;
    @NotBlank
    @Pattern(regexp = "\\S+", message = "login не может содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "birthday не может быть в будущем")
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
