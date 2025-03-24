package ru.stockstrack.authservice.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDTO {
    @NotBlank(message = "Логин не должен быть пустым")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}