package md.cassa.be2048.persistance.repos;

import md.cassa.be2048.persistance.entities.GameEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<GameEntity, Long> {

    @Query("SELECT g FROM GameEntity g WHERE g.player.id = :userId AND g.status = 'IN_PROGRESS' ORDER BY g.createdAt DESC LIMIT 1")
    Optional<GameEntity> findLastGameByUserId(Long userId);

//    @Query("")
//    List<GameEntity> findByUserIdFiltered(Long userId, Specification<GameEntity> specification);
}
