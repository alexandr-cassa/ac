package md.cassa.be2048.persistance.projections;

import java.math.BigDecimal;

public interface PersonalInfoProjection {
    String getUsername();
    Long getTotalGames();
    Long getWonGames();
    Long getLostGames();
    BigDecimal getWinPercentage();
    Long getTotalScores();
}
