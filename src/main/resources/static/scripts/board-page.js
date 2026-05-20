import { game } from "./game.js";
import { boardHandler } from "./board.js";

const LAST_GAME_KEY = "be2048:lastGameId";
const CURRENT_GAME_KEY = "be2048:currentGameId";
const BOARD_KEYS = new Set(["ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight"]);

const TILE_COLORS = {
  0: "#cdc1b4",
  2: "#eee4da",
  4: "#ede0c8",
  8: "#f2b179",
  16: "#f59563",
  32: "#f67c5f",
  64: "#f65e3b",
  128: "#edcf72",
  256: "#edcc61",
  512: "#edc850",
  1024: "#edc53f",
  2048: "#edc22e",
};

function getTileColor(value) {
  return TILE_COLORS[value] || "#3c3a32";
}

function setStatus(message) {
  const node = document.querySelector("#board-status");
  if (node) node.textContent = message || "";
}

function renderBoard() {
  const boardElement = document.querySelector("#board");
  if (!boardElement) return;

  const board = boardHandler.getBoardCopy();
  boardElement.innerHTML = "";

  const scores = document.createElement("div");
  scores.id = "scores";
  scores.textContent = String(game.getOverall().scores);
  boardElement.appendChild(scores);

  for (let row = 0; row < board.length; row++) {
    for (let col = 0; col < board[row].length; col++) {
      const value = board[row][col];
      const cell = document.createElement("div");
      cell.className = "cell";
      cell.textContent = value === 0 ? "" : String(value);
      cell.style.backgroundColor = getTileColor(value);
      boardElement.appendChild(cell);
    }
  }
}

async function handleKeyDown(event) {
  if (!BOARD_KEYS.has(event.key)) return;
  event.preventDefault();

  if (game.isWon() || game.isLost()) return;

  const changed = boardHandler.moveAll(event.key);
  if (!changed) return;

  game.incMoves();
  boardHandler.spawnNewTileRandomly();
  renderBoard();

  if (game.isWon()) {
    setStatus("You won! Saving game...");
    window.removeEventListener("keydown", handleKeyDown);
    await autoSaveGame("You won! Game saved.");
  } else if (game.isLost()) {
    setStatus("No more moves. Saving game...");
    window.removeEventListener("keydown", handleKeyDown);
    await autoSaveGame("No more moves. Game saved.");
  } else {
    setStatus("");
  }
}

function getPayloadForSave(gameId) {
  const overall = game.getOverall();
  return {
    id: gameId ? Number(gameId) : undefined,
    scores: overall.scores,
    moves: overall.moves,
    status: overall.state,
    board: boardHandler.getBoardCopy(),
  };
}

function getUrlParam(name) {
  const params = new URLSearchParams(window.location.search);
  return params.get(name);
}

function initLocalGame() {
  localStorage.removeItem(CURRENT_GAME_KEY);
  setStatus("Game started. Click Save and Exit when ready.");
  renderBoard();
  window.addEventListener("keydown", handleKeyDown);
}

async function loadExistingGame(gameId) {
  try {
    setStatus("Loading game...");
    const loadedGame = await window.api.getGame(gameId);

    // Load the game state
    game.loadState(loadedGame.scores, loadedGame.moves, loadedGame.status);
    boardHandler.loadBoard(loadedGame.board);

    // Store the game ID in localStorage
    localStorage.setItem(CURRENT_GAME_KEY, String(gameId));

    // Render the board
    renderBoard();

    // Set appropriate status message
    if (loadedGame.status === "WON") {
      setStatus("You won! Click Save and Exit to persist.");
    } else if (loadedGame.status === "LOST") {
      setStatus("No more moves. Click Save and Exit to persist.");
    } else {
      setStatus("Game resumed. Click Save and Exit when ready.");
    }

    window.addEventListener("keydown", handleKeyDown);
  } catch (error) {
    setStatus(error?.message || "Failed to load game.");
    // Fall back to new game after 2 seconds
    setTimeout(() => {
      initLocalGame();
    }, 2000);
  }
}

async function autoSaveGame(successMessage) {
  try {
    const existingGameId = localStorage.getItem(CURRENT_GAME_KEY);
    const payload = getPayloadForSave(existingGameId);

    if (existingGameId) {
      await window.api.updateGame(payload);
      localStorage.setItem(LAST_GAME_KEY, String(existingGameId));
    } else {
      const created = await window.api.createGame(payload);
      const newId =
        typeof created === "object" && created !== null ? created.id : created;
      if (newId !== undefined && newId !== null) {
        const idStr = String(newId);
        localStorage.setItem(CURRENT_GAME_KEY, idStr);
        localStorage.setItem(LAST_GAME_KEY, idStr);
      }
    }

    setStatus((successMessage || "Game saved.") + " Redirecting...");
    setTimeout(() => {
      window.location.href = "home.html";
    }, 2000);
  } catch (error) {
    setStatus(error?.message || "Failed to auto-save game.");
  }
}

async function saveAndExit() {
  const saveExitButton = document.querySelector("#save-exit-btn");
  if (!saveExitButton) return;

  saveExitButton.disabled = true;
  saveExitButton.textContent = "Saving...";

  try {
    const existingGameId = localStorage.getItem(CURRENT_GAME_KEY);
    const payload = getPayloadForSave(existingGameId);

    if (existingGameId) {
      await window.api.updateGame(payload);
      localStorage.setItem(LAST_GAME_KEY, String(existingGameId));
    } else {
      const created = await window.api.createGame(payload);

      const newId =
        typeof created === "object" && created !== null ? created.id : created;

      if (newId !== undefined && newId !== null) {
        const idStr = String(newId);
        localStorage.setItem(CURRENT_GAME_KEY, idStr);
        localStorage.setItem(LAST_GAME_KEY, idStr);
      }
    }

    window.location.href = "home.html";
  } catch (error) {
    setStatus(error?.message || "Failed to save game.");
    saveExitButton.disabled = false;
    saveExitButton.textContent = "Save and Exit";
  }
}

async function initBoardPage() {
  const gameIdParam = getUrlParam("gameId");

  if (gameIdParam) {
    // Load existing game
    await loadExistingGame(gameIdParam);
  } else {
    // Start new game
    initLocalGame();
  }

  const saveExitButton = document.querySelector("#save-exit-btn");
  if (saveExitButton) saveExitButton.addEventListener("click", saveAndExit);
}

initBoardPage();
