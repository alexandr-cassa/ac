package md.cassa.be2048.api.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record Game (
   Long id,
   @NotNull Long scores,
   @NotNull Long moves,
   @NotNull Status status,
   @NotEmpty List<List<Integer>> board
) {}
