package ru.stockstrack.authservice.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateDTO {
    private UUID id;

    @Size(min = 3, max = 128, message = "Логин должно содержать от 3 до 128 символов")
    private String login;

    @Email(message = "Email должен быть валидным")
    private String email;

    @Size(min = 8, max = 64, message = "Длина пароля должна быть от 8 до 64 символов")
    private String password;
}
