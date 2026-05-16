package com.example.try1pc.Service;

import com.example.try1pc.DTOs.AuthResponse;
import com.example.try1pc.DTOs.LoginRequest;
import com.example.try1pc.DTOs.UserRegisterRequest;
import com.example.try1pc.Entity.User;
import com.example.try1pc.Exception.UserAlreadyExistsException;
import com.example.try1pc.Exception.UserNotFoundException;
import com.example.try1pc.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Inyecta automáticamente los beans por constructor (Lombok)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final   JwtService jwtService; // El servicio que viene en tus snippets impresos

    public User register(UserRegisterRequest request) {
        // Regla del PDF: Validar username y email únicos [cite: 46-47]
        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("El nombre de usuario o email ya está registrado");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // Encriptación obligatoria antes de guardar [cite: 11-12]
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        // Buscamos al usuario por su email primero (asumiendo que el identificador es el email)
        User user = userRepository.findByEmail(request.getIdentifier())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con el identificador proporcionado"));

        // Spring Security se encarga de verificar la contraseña internamente
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
        );

        // Generamos el token JWT si las credenciales son válidas
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
