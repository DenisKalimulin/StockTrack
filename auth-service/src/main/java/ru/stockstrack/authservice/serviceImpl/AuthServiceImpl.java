package ru.stockstrack.authservice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.stockstrack.authservice.DTO.LoginRequestDTO;
import ru.stockstrack.authservice.DTO.LoginResponseDTO;
import ru.stockstrack.authservice.DTO.UserRegistrationDTO;
import ru.stockstrack.authservice.DTO.UserResponseDTO;
import ru.stockstrack.authservice.config.JwtTokenProvider;
import ru.stockstrack.authservice.exception.InvalidEmailOrPasswordException;
import ru.stockstrack.authservice.exception.UserAlreadyExistsException;
import ru.stockstrack.authservice.mappers.UserMapper;
import ru.stockstrack.authservice.models.User;
import ru.stockstrack.authservice.repository.UserRepository;
import ru.stockstrack.authservice.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Transactional
    @Override
    public UserResponseDTO register(UserRegistrationDTO userRegistrationDTO) {
        logger.info("Регистрация нового пользователя");
        checkUserUniqueness(userRegistrationDTO.getLogin(), userRegistrationDTO.getEmail());

        User user = User.builder()
                .login(userRegistrationDTO.getLogin().toLowerCase())
                .email(userRegistrationDTO.getEmail().toLowerCase())
                .password(passwordEncoder.encode(userRegistrationDTO.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        logger.info("Пользователь зарегистрирован");

        return userMapper.toResponse(savedUser);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        logger.info("Попытка входа пользователя");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getLogin().toLowerCase(),
                            loginRequestDTO.getPassword().toLowerCase()
                    )
            );
        } catch (BadCredentialsException e) {
            logger.warn("Ошибка аутентификации: неверный логин или пароль");
            throw new InvalidEmailOrPasswordException("Неверный логин или пароль");
        }

        String token = jwtTokenProvider.generateToken(loginRequestDTO.getLogin().toLowerCase());

        logger.info("Пользователь успешно вошел в систему");
        return LoginResponseDTO.builder()
                .message("Успешный вход")
                .token(token).
                build();
    }

    /**
     * Проверка уникальности логина и email.
     * Если логин или email уже заняты, выбрасывает исключение.
     *
     * @param login логин пользователя.
     * @param email email пользователя.
     * @throws UserAlreadyExistsException если логин или email уже заняты.
     */
    private void checkUserUniqueness(String login, String email) {
        if (login != null && userRepository.findByLogin(login.toLowerCase()).isPresent()) {
            logger.warn("Попытка регистрации с уже существующим логином");
            throw new UserAlreadyExistsException("Логин уже используется");
        }
        if (email != null && userRepository.findByEmail(email.toLowerCase()).isPresent()) {
            logger.warn("Попытка регистрации с уже существующим email");
            throw new UserAlreadyExistsException("Email уже используется");
        }
    }
}