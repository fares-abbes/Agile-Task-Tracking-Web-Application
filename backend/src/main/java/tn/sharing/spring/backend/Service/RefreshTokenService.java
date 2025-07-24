package tn.sharing.spring.backend.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sharing.spring.backend.Entity.RefreshToken;
import tn.sharing.spring.backend.Entity.Users;
import tn.sharing.spring.backend.Repository.RefreshTokenRepo;
import tn.sharing.spring.backend.Repository.UserRepo;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;
    private final long refreshTokenDurationMs = 7 * 24 * 60 * 60 * 1000; // 7 days

    public RefreshToken createRefreshToken(int userId) {
        Users user = userRepo.findById(userId).orElseThrow();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        return refreshTokenRepo.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    public boolean isExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public void deleteByUserId(int userId) {
        Users user = userRepo.findById(userId).orElseThrow();
        refreshTokenRepo.deleteByUser(user);
    }

    public void deleteByToken(String token) {
        refreshTokenRepo.findByToken(token).ifPresent(refreshTokenRepo::delete);
    }
}
