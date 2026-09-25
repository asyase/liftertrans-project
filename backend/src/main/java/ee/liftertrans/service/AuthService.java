package ee.liftertrans.service;
import ee.liftertrans.dto.AuthRequestDto;
import ee.liftertrans.dto.AuthResponseDto;
import ee.liftertrans.infrastructure.exception.UnauthorizedException;
import ee.liftertrans.mapper.UserMapper;
import ee.liftertrans.persistence.entity.User;
import ee.liftertrans.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

//AuthRequestDto = mis tüüpi objekt
//request = kuidas me seda objekti selles meetodis nimetame

public class AuthService {


private final UserRepository userRepository;
private final UserMapper userMapper;

public AuthResponseDto login(AuthRequestDto authRequestDto) {

    // 1. Leia kasutaja emaili järgi
    String email = authRequestDto.getEmail();
    String password = authRequestDto.getPassword();

    User user = userRepository.findUserBy(email, password, "A")
            .orElseThrow(()-> new UnauthorizedException(
                    "Vale e-post või parool",
                    "INCORRECT_CREDENTIALS"
                    )
            );



    return userMapper.toAuthResponseDto(user);
}
    }

//Service otsustab, et on viga, exception handler teeb sellest ApiError + õige HTTP staatuse.








