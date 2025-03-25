package ru.stockstrack.authservice.serviceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.stockstrack.authservice.DTO.LoginRequestDTO;
import ru.stockstrack.authservice.DTO.LoginResponseDTO;
import ru.stockstrack.authservice.DTO.UserRegistrationDTO;
import ru.stockstrack.authservice.DTO.UserResponseDTO;
import ru.stockstrack.authservice.config.JwtTokenProvider;
import ru.stockstrack.authservice.mappers.UserMapper;
import ru.stockstrack.authservice.models.User;
import ru.stockstrack.authservice.repository.UserRepository;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldRegisterUserSuccessfully() {
        // Подготовка данных
        UUID userId = UUID.randomUUID();  // Генерация уникального ID
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO("TestUser", "test@example.com", "password123");
        User user = User.builder()
                .id(userId)  // Используем UUID
                .login("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
        UserResponseDTO userResponseDTO = new UserResponseDTO(userId, "testuser", "test@example.com");

        // Мокируем зависимости
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponseDTO);

        // Вызов метода
        UserResponseDTO result = authService.register(userRegistrationDTO);

        // Проверки
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(userRegistrationDTO.getPassword());
        assertEquals(userResponseDTO, result);
    }

    @Test
    void shouldLoginUserSuccessfully() {
        // Подготовка данных
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("TestUser", "password123");
        String token = "validToken";
        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .message("Успешный вход")
                .token(token)
                .build();

        // Мокируем зависимости
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("TestUser", "password123"));
        when(jwtTokenProvider.generateToken(anyString())).thenReturn(token);

        // Вызов метода
        LoginResponseDTO result = authService.login(loginRequestDTO);

        // Проверки
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(loginRequestDTO.getLogin().toLowerCase());
        assertEquals(loginResponseDTO, result);
    }
}

