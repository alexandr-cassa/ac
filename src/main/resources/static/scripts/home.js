const LAST_GAME_KEY = "be2048:lastGameId";
const CURRENT_GAME_KEY = "be2048:currentGameId";

function setOldGameDisabled(oldGameButton, oldGameHint, message) {
  oldGameButton.classList.add("home-btn-disabled");
  oldGameButton.setAttribute("aria-disabled", "true");
  oldGameButton.href = "#";
  oldGameHint.textContent = message;
}

function setOldGameEnabled(oldGameButton, oldGameHint, gameId) {
  oldGameButton.classList.remove("home-btn-disabled");
  oldGameButton.setAttribute("aria-disabled", "false");
  oldGameButton.href = `board.html?gameId=${encodeURIComponent(gameId)}`;
  oldGameHint.textContent = "Resume your previous game.";
}

async function setupOldGameState() {
  const oldGameButton = document.querySelector("#old-game-btn");
  const oldGameHint = document.querySelector("#old-game-hint");
  if (!oldGameButton || !oldGameHint) return;

  setOldGameDisabled(oldGameButton, oldGameHint, "Checking for previous game...");

  try {
    const lastGame = await window.api.getLastGame();

    // Treat empty/blank payloads as "no last game".
    const isEmptyObject =
      lastGame && typeof lastGame === "object" && Object.keys(lastGame).length === 0;
    const isEmptyString = typeof lastGame === "string" && lastGame.trim().length === 0;
    if (!lastGame || isEmptyObject || isEmptyString) {
      localStorage.removeItem(LAST_GAME_KEY);
      setOldGameDisabled(oldGameButton, oldGameHint, "No previous game found yet.");
      return;
    }

    const gameId = lastGame.id;
    if (!gameId) {
      localStorage.removeItem(LAST_GAME_KEY);
      setOldGameDisabled(oldGameButton, oldGameHint, "No previous game found yet.");
      return;
    }

    const gameIdString = String(gameId);
    localStorage.setItem(LAST_GAME_KEY, gameIdString);
    setOldGameEnabled(oldGameButton, oldGameHint, gameIdString);
  } catch (_) {
    localStorage.removeItem(LAST_GAME_KEY);
    setOldGameDisabled(oldGameButton, oldGameHint, "Unable to load previous game.");
  }
}

function setupNavigationPlaceholders() {
  const profileButton = document.querySelector("#profile-btn");
  const newGameButton = document.querySelector("#new-game-btn");
  const leaderboardsButton = document.querySelector("#leaderboards-btn");
  const oldGameButton = document.querySelector("#old-game-btn");

  if (profileButton) {
    profileButton.href = "profile.html";
  }

  if (newGameButton) {
    newGameButton.href = "board.html";
  }

  if (leaderboardsButton) {
    leaderboardsButton.href = "leaderboard.html";
  }

  if (oldGameButton) {
    oldGameButton.addEventListener("click", (event) => {
      if (oldGameButton.getAttribute("aria-disabled") === "true") {
        event.preventDefault();
      }
    });
  }
}

function setupLogout() {
  const logoutButton = document.querySelector("#logout-btn");
  if (!logoutButton) return;

  logoutButton.addEventListener("click", async () => {
    logoutButton.disabled = true;
    logoutButton.textContent = "Logging out...";

    try {
      if (window.api && typeof window.api.logout === "function") {
        await window.api.logout();
      }
    } catch (_) {
      // Continue local cleanup and redirect even if backend logout fails.
    } finally {
      localStorage.removeItem(LAST_GAME_KEY);
      localStorage.removeItem(CURRENT_GAME_KEY);
      window.location.href = "login.html";
    }
  });
}

setupOldGameState();
setupNavigationPlaceholders();
setupLogout();
