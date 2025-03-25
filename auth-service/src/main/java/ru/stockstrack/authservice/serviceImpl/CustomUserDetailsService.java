package ru.stockstrack.authservice.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.stockstrack.authservice.models.User;
import ru.stockstrack.authservice.repository.UserRepository;
import ru.stockstrack.authservice.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login.toLowerCase())
                .orElseThrow(() ->
                        new UsernameNotFoundException("Пользователь с логином " + login + " не найден"));
        return new CustomUserDetails(user);
    }
}