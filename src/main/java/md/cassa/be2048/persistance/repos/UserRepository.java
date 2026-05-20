package md.cassa.be2048.persistance.repos;

import jakarta.validation.constraints.NotBlank;
import md.cassa.be2048.persistance.entities.UserEntity;
import md.cassa.be2048.persistance.projections.LeaderboardProjection;
import md.cassa.be2048.persistance.projections.PersonalInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(@NotBlank String username);

    @Query(
            "SELECT u.username as username, SUM(g.scores) as scores " +
            "FROM GameEntity g JOIN UserEntity u ON g.player.id = u.id " +
            "WHERE g.status = 'WON' " +
            "GROUP BY u.username " +
            "ORDER BY SUM(g.scores) DESC " +
            "LIMIT 10"
    )
    List<LeaderboardProjection> getTop10Players();

    @Query(
            "SELECT " +
                    "u.username as username, " +
                    "SUM(g.scores) as totalScores, " +
                    "COUNT(g.id) as totalGames, " +
                    "SUM(CASE WHEN g.status = 'WON' THEN 1 ELSE 0 END) as wonGames, " +
                    "SUM(CASE WHEN g.status = 'LOST' THEN 1 ELSE 0 END) as lostGames, " +
                    "CAST(SUM(CASE WHEN g.status = 'WON' THEN 1 ELSE 0 END) AS FLOAT) / NULLIF(COUNT(g.id), 0) as winPercentage " +
                    "FROM UserEntity u JOIN GameEntity g ON u.id = g.player.id " +
                    "WHERE u.id = :userId " +
                    "GROUP BY u.username"
    )
    Optional<PersonalInfoProjection> getPersonalInfo(Long userId);
}
