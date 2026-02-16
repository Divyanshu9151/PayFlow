package com.payflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return  new BCryptPasswordEncoder(); //we can pass work factor in this to decrease the speed
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //http
//                .csrf(csrf -> csrf.disable())
//                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/users/**",
//                                "/wallets/**"
//                        ).permitAll()
//                        .anyRequest().denyAll()
//                )
//                .formLogin(form -> form.disable())
//                .httpBasic(basic -> basic.disable());
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // 🔥 IMPORTANT

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception
    {
        return configuration.getAuthenticationManager();
    }
}

