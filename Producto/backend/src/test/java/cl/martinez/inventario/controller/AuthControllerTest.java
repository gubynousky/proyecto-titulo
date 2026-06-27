package cl.martinez.inventario.controller;

import cl.martinez.inventario.dto.AuthRequest;
import cl.martinez.inventario.dto.AuthResponse;
import cl.martinez.inventario.dto.RegistroRequest;
import cl.martinez.inventario.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController — Pruebas Unitarias")
class AuthControllerTest {

    @Mock private AuthService authService;
    @InjectMocks private AuthController authController;

    @Test
    @DisplayName("Login exitoso retorna JWT y datos del usuario")
    void login_exitoso() {
        AuthResponse expected = new AuthResponse("jwt-token-test", "test@correo.com", "Ruby Test", "ADMIN");
        when(authService.login(any(AuthRequest.class))).thenReturn(expected);

        AuthRequest request = new AuthRequest();
        request.setEmail("test@correo.com");
        request.setPassword("password123");

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertNotNull(response.getBody());
        assertEquals("jwt-token-test", response.getBody().getToken());
        assertEquals("test@correo.com", response.getBody().getEmail());
        assertEquals("Ruby Test", response.getBody().getNombre());
        assertEquals("ADMIN", response.getBody().getRol());
    }

    @Test
    @DisplayName("Registro exitoso retorna JWT y datos del nuevo usuario")
    void registro_exitoso() {
        AuthResponse expected = new AuthResponse("jwt-nuevo", "nuevo@correo.com", "Nuevo User", "ADMIN");
        when(authService.registro(any(RegistroRequest.class))).thenReturn(expected);

        RegistroRequest request = new RegistroRequest();
        request.setNombre("Nuevo User");
        request.setEmail("nuevo@correo.com");
        request.setPassword("password123");

        ResponseEntity<AuthResponse> response = authController.registro(request);

        assertNotNull(response.getBody());
        assertEquals("jwt-nuevo", response.getBody().getToken());
        assertEquals("Nuevo User", response.getBody().getNombre());
        verify(authService).registro(any(RegistroRequest.class));
    }

    @Test
    @DisplayName("Login invoca al servicio exactamente una vez")
    void login_invocaServicio() {
        when(authService.login(any())).thenReturn(new AuthResponse("t", "e", "n", "ADMIN"));

        AuthRequest request = new AuthRequest();
        request.setEmail("x@x.com");
        request.setPassword("123");

        authController.login(request);

        verify(authService, times(1)).login(any(AuthRequest.class));
    }
}
