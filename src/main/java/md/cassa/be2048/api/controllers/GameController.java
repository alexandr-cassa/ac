package md.cassa.be2048.api.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import md.cassa.be2048.annotations.UserId;
import md.cassa.be2048.api.models.Game;
import md.cassa.be2048.services.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<Long> initGame(@Valid @RequestBody Game game, @UserId Long userId) {
        return ResponseEntity.ok(gameService.createGame(game, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(@PathVariable Long id, @UserId Long userId) {
        return ResponseEntity.ok(gameService.getGame(id, userId));
    }

    @GetMapping("/last")
    public ResponseEntity<Game> getGame(@UserId Long userId) {
        return ResponseEntity.ok(gameService.getLastGame(userId));
    }

    @PutMapping
    public ResponseEntity<Game> updateGame(@Valid @RequestBody Game game, @UserId Long userId) {
        return ResponseEntity.ok(gameService.updateGame(game, userId));
    }
}
