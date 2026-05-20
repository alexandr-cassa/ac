package md.cassa.be2048.api.models;

import java.math.BigDecimal;

public record PersonalInfo (
        String username,
        Long totalGames,
        Long wonGames,
        Long lostGames,
        BigDecimal winPercentage,
        Long totalScores,
        Long totalMoves
) {
}
