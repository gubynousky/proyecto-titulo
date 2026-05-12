package cl.martinez.inventario.service;

import cl.martinez.inventario.dto.*;
import cl.martinez.inventario.exception.BadRequestException;
import cl.martinez.inventario.model.Usuario;
import cl.martinez.inventario.repository.UsuarioRepository;
import cl.martinez.inventario.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(AuthRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Email o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BadRequestException("Email o contraseña incorrectos");
        }

        if (!usuario.getActivo()) {
            throw new BadRequestException("Usuario desactivado");
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().name());
        return new AuthResponse(token, usuario.getEmail(), usuario.getNombre(), usuario.getRol().name());
    }

    public AuthResponse registro(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Usuario.Rol.ADMIN)
                .activo(true)
                .build();

        usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().name());
        return new AuthResponse(token, usuario.getEmail(), usuario.getNombre(), usuario.getRol().name());
    }
}
