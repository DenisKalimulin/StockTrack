package ru.stockstrack.authservice.serviceImpl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.stockstrack.authservice.DTO.UserResponseDTO;
import ru.stockstrack.authservice.exception.UserNotFoundException;
import ru.stockstrack.authservice.mappers.UserMapper;
import ru.stockstrack.authservice.models.User;
import ru.stockstrack.authservice.repository.UserRepository;
import ru.stockstrack.authservice.security.SecurityUtils;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldReturnUserWhenUserExists() {
        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            // 1. Подготовка
            User user = new User();
            user.setLogin("test");
            UserResponseDTO dto = new UserResponseDTO();
            dto.setLogin("test");

            // 2. Настройка статического метода
            mockedSecurity.when(SecurityUtils::getCurrentLogin).thenReturn("test");

            // 3. Настройка моков
            Mockito.when(userRepository.findByLogin("test")).thenReturn(Optional.of(user));
            Mockito.when(userMapper.toResponse(user)).thenReturn(dto);

            // 4. Вызов
            UserResponseDTO result = userService.getCurrentUserProfile();

            // 5. Проверка
            Assertions.assertEquals("test", result.getLogin());
            Mockito.verify(userRepository).findByLogin("test");
            Mockito.verify(userMapper).toResponse(user);
        }
    }

    @Test
    void shouldThrowExceptionWhenUserNotLoggedIn() {
        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentLogin).thenReturn(null);

            Assertions.assertThrows(UserNotFoundException.class, () -> userService.getCurrentUserProfile());
        }
    }

    @Test
    void shouldDeleteUserWhenUserExists() {
        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            User user = new User();
            user.setLogin("test"); // Подготовка


            mockedSecurity.when(SecurityUtils::getCurrentLogin).thenReturn("test"); // Настройка статик метода

            Mockito.when(userRepository.findByLogin("test")).thenReturn(Optional.of(user)); // Настройка моков
            Mockito.doNothing().when(userRepository).delete(user);

            userService.deleteCurrentUser(); // Вызов

            Mockito.verify(userRepository).delete(user); // Проверка
        }
    }

    @Test
    void shouldDeleteUserWhenUserNotLoggedIn() {
        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentLogin).thenReturn(null);

            Assertions.assertThrows(UserNotFoundException.class, () -> userService.deleteCurrentUser());
        }
    }
}