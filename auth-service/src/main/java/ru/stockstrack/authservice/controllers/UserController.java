package ru.stockstrack.authservice.controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stockstrack.authservice.DTO.UserResponseDTO;
import ru.stockstrack.authservice.DTO.UserUpdateDTO;
import ru.stockstrack.authservice.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {

        UserResponseDTO updatedUser = userService.updateUser(userUpdateDTO);
        logger.info("Профиль пользователя был успешно обновлен");

        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        logger.info("Запрос на получение профиля");
        UserResponseDTO userResponseDTO = userService.getCurrentUserProfile();
        return ResponseEntity.ok(userResponseDTO);
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUser() {
        userService.deleteCurrentUser();
        logger.info("Пользователь был успешно удален из системы");
        return ResponseEntity.ok("Профиль удален");
    }
}