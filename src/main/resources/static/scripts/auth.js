async function redirectIfAuthenticated() {
  try {
    await window.api.getPersonalInfo();
    window.location.href = "home.html";
  } catch {
    // not authenticated, stay on login page
  }
}

function setMessage(element, message, isError = false) {
  element.textContent = message;
  element.classList.toggle("auth-message-error", isError);
  element.classList.toggle("auth-message-success", !isError && message.length > 0);
}

function initLoginForm() {
  const form = document.querySelector("#login-form");
  if (!form) return;

  const message = document.querySelector("#auth-message");
  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    setMessage(message, "Signing in...");

    const username = form.username.value.trim();
    const password = form.password.value;

    try {
      await window.api.login(username, password);
      setMessage(message, "Login successful. Redirecting...");
      window.location.href = "home.html";
    } catch (error) {
      setMessage(message, error.message || "Login failed", true);
    }
  });
}

function initRegisterForm() {
  const form = document.querySelector("#register-form");
  if (!form) return;

  const message = document.querySelector("#auth-message");
  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    setMessage(message, "Creating account...");

    const username = form.username.value.trim();
    const password = form.password.value;

    try {
      await window.api.register(username, password);
      setMessage(message, "Account created. Redirecting to login...");
      setTimeout(() => {
        window.location.href = "login.html";
      }, 400);
    } catch (error) {
      setMessage(message, error.message || "Registration failed", true);
    }
  });
}

redirectIfAuthenticated();
initLoginForm();
initRegisterForm();

