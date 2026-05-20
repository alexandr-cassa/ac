const API_BASE = window.location.origin;

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  });

  const contentType = response.headers.get("content-type") || "";
  const body = contentType.includes("application/json")
    ? await response.json().catch(() => ({}))
    : await response.text().catch(() => "");

  if (!response.ok) {
    const message =
      (body && typeof body === "object" && (body.message || body.error)) ||
      (typeof body === "string" && body) ||
      `Request failed with status ${response.status}`;
    throw new Error(message);
  }

  return body;
}

async function login(username, password) {
  return request("/users/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
}

async function register(username, password) {
  return request("/users/register", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
}

async function logout() {
  return request("/users/logout", {
    method: "POST",
  });
}

async function createGame(game) {
  return request("/games", {
    method: "POST",
    body: JSON.stringify(game),
  });
}

async function getGame(id) {
  return request(`/games/${encodeURIComponent(id)}`, {
    method: "GET",
  });
}

async function updateGame(game) {
  return request("/games", {
    method: "PUT",
    body: JSON.stringify(game),
  });
}

async function getLastGame() {
  return request("/games/last", {
    method: "GET",
  });
}

async function getLeaderboard() {
  return request("/users/leaderboard", {
    method: "GET",
  });
}

async function getPersonalInfo() {
  return request("/users/personal-info", {
    method: "GET",
  });
}

window.api = {
  login,
  register,
  logout,
  createGame,
  getGame,
  getLastGame,
  updateGame,
  getLeaderboard,
  getPersonalInfo,
};
