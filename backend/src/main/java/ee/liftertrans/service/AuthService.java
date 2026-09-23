package ee.liftertrans.service;

import ee.liftertrans.controller.AuthRequestDto;
import ee.liftertrans.controller.dto.AuthResponseDto;
import ee.liftertrans.infrastructure.error.ApiError;
import ee.liftertrans.infrastructure.exception.ForbiddenException;
import ee.liftertrans.infrastructure.exception.UnauthorizedException;
import ee.liftertrans.mapper.UserMapper;
import ee.liftertrans.persistence.entity.User;
import ee.liftertrans.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        String email = authRequestDto.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Vale e-post või parool", "INCORRECT_CREDENTIALS"));

        if (!passwordEncoder.matches(
                authRequestDto.getPassword(),
                user.getPasswordHash()
        ))
            throw new UnauthorizedException(
                    "Vale e-post või parool", "INCORRECT_CREDENTIALS"
            );

        return userMapper.toAuthResponseDto(user);
    }
}

