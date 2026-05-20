package md.cassa.be2048.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import md.cassa.be2048.api.models.User;
import md.cassa.be2048.constants.Security;
import md.cassa.be2048.exceptions.NotFoundException;
import md.cassa.be2048.exceptions.WrongCredentialsException;
import md.cassa.be2048.persistance.entities.UserEntity;
import md.cassa.be2048.persistance.repos.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String SESSION_PREFIX = "session:";

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.session-ttl-hours:2}")
    private long sessionTtlHours;

    public ResponseCookie login(User user) {
        final UserEntity userEntity = userRepository.findByUsername(user.username())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!userEntity.getPassword().equals(user.password())) {
            throw new WrongCredentialsException();
        }

        final UUID uuid = UUID.randomUUID();
        redisTemplate.opsForValue().set(
                SESSION_PREFIX + uuid,
                String.valueOf(userEntity.getId()),
                sessionTtlHours,
                TimeUnit.HOURS
        );

        return ResponseCookie.from("SessionID", uuid.toString())
                .httpOnly(true)
                .path("/")
                .maxAge(sessionTtlHours * 3600)
                .build();
    }

    public void evictSession(UUID id) {
        redisTemplate.delete(SESSION_PREFIX + id);
    }

    public boolean isAuthenticated(UUID id) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(SESSION_PREFIX + id));
    }

    public Long getUserIdBySession(UUID id) {
        final String value = redisTemplate.opsForValue().get(SESSION_PREFIX + id);
        return value != null ? Long.parseLong(value) : null;
    }

    public String getSessionFromRequest(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (Security.AUTH_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
