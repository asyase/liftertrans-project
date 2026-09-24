package ee.liftertrans.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Vajadusel API päringute jaoks
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated() // Kõik päringud nõuavad autentimist
                );

        return http.build();
    }
}
}
