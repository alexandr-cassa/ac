package md.cassa.be2048.api.models;

import jakarta.validation.constraints.NotBlank;

public record User(
        Long id,
        @NotBlank String username,
        @NotBlank String password
) {
    public User (Long id, String username) {
        this(id, username, "");
    }
}
