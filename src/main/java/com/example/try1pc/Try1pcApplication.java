package com.example.try1pc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootApplication
public class Try1pcApplication {
    @Bean
    public UserDetailsService userDetailsService(com.example.try1pc.Repository.UserRepository userRepository) {
        return username -> userRepository.findByEmail(username) // Buscamos por email tal cual tu login
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("Usuario no encontrado"));
    }
    public static void main(String[] args) {
        SpringApplication.run(Try1pcApplication.class, args);
    }

}
