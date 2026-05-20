package md.cassa.be2048.services;

import lombok.RequiredArgsConstructor;
import md.cassa.be2048.api.mappers.UserMapper;
import md.cassa.be2048.api.models.User;
import md.cassa.be2048.exceptions.NotFoundException;
import md.cassa.be2048.persistance.entities.UserEntity;
import md.cassa.be2048.persistance.projections.LeaderboardProjection;
import md.cassa.be2048.persistance.projections.PersonalInfoProjection;
import md.cassa.be2048.persistance.repos.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Long createUser(User user) {
        final UserEntity userEntity = userMapper.toEntity(user);
        return userRepository.save(userEntity).getId();
    }

    public List<LeaderboardProjection> getLeaderboard() {
        return userRepository.getTop10Players();
    }

    public PersonalInfoProjection getPersonalInfo(Long userId) {
        return userRepository.getPersonalInfo(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
