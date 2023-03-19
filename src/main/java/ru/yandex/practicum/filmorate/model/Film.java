package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Integer id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @Size(max = 200, message = "description не может содержать больше 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @Positive(message = "duration не может быть отрицательным или равным нулю")
    private Long duration;
}
