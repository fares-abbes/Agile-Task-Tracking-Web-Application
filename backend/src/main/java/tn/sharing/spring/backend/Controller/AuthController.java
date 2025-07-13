package tn.sharing.spring.backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.sharing.spring.backend.DTOs.LoginRequest;
import tn.sharing.spring.backend.Entity.RefreshToken;
import tn.sharing.spring.backend.Entity.Users;
import tn.sharing.spring.backend.Security.JwtUtils;
import tn.sharing.spring.backend.Service.RefreshTokenService;
import tn.sharing.spring.backend.Repository.UserRepo;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            UserRepo userRepository,
            PasswordEncoder passwordEncoder,
            RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwtToken((UserDetails) authentication.getPrincipal());

        // Find the user entity
        Users user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        // Create a refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken((long) user.getId());

        return ResponseEntity.ok(Map.of(
                "accessToken", token,
                "refreshToken", refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshTokenStr = request.get("refreshToken");
        var refreshTokenOpt = refreshTokenService.findByToken(refreshTokenStr);
        if (refreshTokenOpt.isEmpty() || refreshTokenService.isExpired(refreshTokenOpt.get())) {
            return ResponseEntity.status(403).body("Invalid or expired refresh token");
        }
        Users user = refreshTokenOpt.get().getUser();
        String newAccessToken = jwtUtils.generateJwtTokenFromUser(user);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        String refreshTokenStr = request.get("refreshToken");
        refreshTokenService.deleteByToken(refreshTokenStr);
        return ResponseEntity.ok("Logged out successfully.");
    }

}
