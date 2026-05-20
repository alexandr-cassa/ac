package md.cassa.be2048.services;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import md.cassa.be2048.api.mappers.GameMapper;
import md.cassa.be2048.api.models.Game;
import md.cassa.be2048.api.models.Status;
import md.cassa.be2048.exceptions.NotFoundException;
import md.cassa.be2048.persistance.entities.GameEntity;
import md.cassa.be2048.persistance.entities.UserEntity;
import md.cassa.be2048.persistance.repos.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class GameService {

    private static final EnumSet<Status> IMMUTABLE_STATUSES = EnumSet.of(Status.LOST, Status.WON);

    private final GameRepository gameRepository;
    private final EntityManager entityManager;
    private final GameMapper gameMapper;

    @Transactional
    public Long createGame(Game game, Long userId) {
        final GameEntity gameEntity = gameMapper.toEntity(game);
        final UserEntity userProxy = entityManager.getReference(UserEntity.class, userId);
        gameEntity.setPlayer(userProxy);

        gameRepository.findLastGameByUserId(userId)
                .ifPresent(previousGame -> previousGame.setStatus(Status.LOST));

        return  gameRepository.save(gameEntity).getId();
    }

    @Transactional(readOnly = true)
    public Game getGame(Long gameId, Long userId) {
        final GameEntity gameEntity = gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("Game with id: " + gameId + " not found"));

        if (!gameEntity.getPlayer().getId().equals(userId)) {
            throw new NotFoundException("Game with id: " + gameId + " not found");
        }

        return gameMapper.toDto(gameEntity);
    }

    @Transactional
    public Game updateGame(Game game, Long userId) {
        final GameEntity gameEntity = gameRepository.findById(game.id())
                .orElseThrow(() -> new NotFoundException("Game with id: " + game.id() + " not found"));

        if (!gameEntity.getPlayer().getId().equals(userId)) {
            throw new NotFoundException("Game with id: " + game.id() + " not found");
        }

        if (IMMUTABLE_STATUSES.contains(gameEntity.getStatus())) {
            throw new IllegalStateException(
                    "Game with id: " + game.id() + " is finished. You are not allowed to update it"
            );
        }

        gameMapper.updateEntity(game, gameEntity);
        gameRepository.save(gameEntity);

        return gameMapper.toDto(gameEntity);
    }

    @Transactional(readOnly = true)
    public Game getLastGame(Long userId) {
        final GameEntity lastGame = gameRepository.findLastGameByUserId(userId)
                .orElseThrow(() -> new NotFoundException("There is no active games for user: " + userId));

        return gameMapper.toDto(lastGame);
    }
}
