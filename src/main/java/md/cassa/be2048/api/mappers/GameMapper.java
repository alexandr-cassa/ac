package md.cassa.be2048.api.mappers;

import md.cassa.be2048.api.models.Game;
import md.cassa.be2048.persistance.entities.GameEntity;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    /**
     * Maps a GameEntity to a Game DTO
     *
     * @param gameEntity the game entity to map
     * @return the mapped Game DTO
     */
    public Game toDto(GameEntity gameEntity) {
        if (gameEntity == null) {
            return null;
        }
        return new Game(
                gameEntity.getId(),
                gameEntity.getScores(),
                gameEntity.getMoves(),
                gameEntity.getStatus(),
                gameEntity.getBoard()
        );
    }

    /**
     * Maps a Game DTO to a GameEntity
     *
     * @param game the game DTO to map
     * @return the mapped GameEntity
     */
    public GameEntity toEntity(Game game) {
        if (game == null) {
            return null;
        }
        return GameEntity.builder()
                .id(game.id())
                .scores(game.scores())
                .moves(game.moves())
                .status(game.status())
                .board(game.board())
                .build();
    }

    /**
     * Updates an existing GameEntity with data from a Game DTO
     * Only sets fields if they are not null
     *
     * @param game the source Game DTO
     * @param gameEntity the target GameEntity to update
     */
    public void updateEntity(Game game, GameEntity gameEntity) {
        if (game == null || gameEntity == null) {
            return;
        }
        if (game.scores() != null) {
            gameEntity.setScores(game.scores());
        }
        if (game.moves() != null) {
            gameEntity.setMoves(game.moves());
        }
        if (game.status() != null) {
            gameEntity.setStatus(game.status());
        }
        if (game.board() != null && !game.board().isEmpty()) {
            gameEntity.setBoard(game.board());
        }
    }
}

