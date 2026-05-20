package md.cassa.be2048.api.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.cassa.be2048.annotations.UserId;
import md.cassa.be2048.api.models.User;
import md.cassa.be2048.persistance.projections.LeaderboardProjection;
import md.cassa.be2048.persistance.projections.PersonalInfoProjection;
import md.cassa.be2048.services.AuthService;
import md.cassa.be2048.services.UserService;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Long> registerUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody User user, HttpServletResponse response) {
        final ResponseCookie cookie = authService.login(user);
        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(cookie.toString());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        final String session = authService.getSessionFromRequest(request);
        if (session != null) {
            authService.evictSession(java.util.UUID.fromString(session));
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/personal-info")
    public ResponseEntity<PersonalInfoProjection> personalInfo(@UserId Long userId) {
        return ResponseEntity.ok(userService.getPersonalInfo(userId));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardProjection>> leaderBord() {
        return ResponseEntity.ok(userService.getLeaderboard());
    }
}
