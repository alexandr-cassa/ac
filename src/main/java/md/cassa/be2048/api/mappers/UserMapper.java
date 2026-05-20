package md.cassa.be2048.api.mappers;

import md.cassa.be2048.api.models.User;
import md.cassa.be2048.persistance.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Maps a UserEntity to a User DTO
     *
     * @param userEntity the user entity to map
     * @return the mapped User DTO
     */
    public User toDto(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return new User(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPassword()
        );
    }

    /**
     * Maps a User DTO to a UserEntity
     *
     * @param user the user DTO to map
     * @return the mapped UserEntity
     */
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.id());
        userEntity.setUsername(user.username());
        userEntity.setPassword(user.password());
        return userEntity;
    }

    /**
     * Updates an existing UserEntity with data from a User DTO
     * Only sets fields if they are not null and not blank
     *
     * @param user the source User DTO
     * @param userEntity the target UserEntity to update
     */
    public void updateEntity(User user, UserEntity userEntity) {
        if (user == null || userEntity == null) {
            return;
        }
        if (user.username() != null && !user.username().isBlank()) {
            userEntity.setUsername(user.username());
        }
        if (user.password() != null && !user.password().isBlank()) {
            userEntity.setPassword(user.password());
        }
    }
}

