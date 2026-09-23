package ee.liftertrans.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//@Bean – Spring loob objekti ja hakkab seda haldama.
//
//PasswordEncoder – interface ehk tüüp, mida teised klassid saavad kasutada.
//
//passwordEncoder() – meetodi nimi.
//
//new BCryptPasswordEncoder() – loob objekti, mis oskab paroole BCryptiga räsida ja kontrollida

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}