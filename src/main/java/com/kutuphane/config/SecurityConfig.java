 /*package com.kutuphane.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Herkese açık sayfalar (login, register, ana sayfa, API'ler, statik dosyalar)
                        .requestMatchers("/", "/login", "/register", "/api/users/register", "/css/**", "/js/**").permitAll()
                        // Geriye kalan tüm istekler için kimlik doğrulaması iste
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Özel login sayfamızın yolu
                        .loginProcessingUrl("/login") // Formun gönderileceği adres (Spring Security bunu yönetir)
                        .defaultSuccessUrl("/main", true) // Giriş başarılıysa YÖNLENDİRİLECEK SAYFA
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") // Çıkış yapıldığında login sayfasına yönlendir
                        .permitAll()
                );

        return http.build();
    }
}*/