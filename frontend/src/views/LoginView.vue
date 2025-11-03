<template>
  <div class="login-container">
    <h2>Bejelentkezés</h2>

    <form @submit.prevent="handleLogin">

      <div class="form-group">
        <label for="username">Felhasználónév</label>
        <input type="text" id="username" v-model="username" required>
      </div>

      <div class="form-group">
        <label for="password">Jelszó</label>
        <input type="password" id="password" v-model="password" required>
      </div>

      <div v-if="captchaImage" class="captcha-group">
        <label>CAPTCHA Ellenőrzés</label>
        <img :src="'data:image/jpeg;base64,' + captchaImage" alt="CAPTCHA kép a bejelentkezéshez" class="captcha-image">
        <input type="text" v-model="captchaAnswer" placeholder="Írja be a fenti kódot" required>
      </div>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

      <button type="submit" :disabled="!isFormValid" :class="{ 'disabled-button': !isFormValid }">Belépés</button>
    </form>

    <router-link to="/register">Nincs még fiókja? Regisztráljon!</router-link>
  </div>
</template>

<script setup>
import { ref,computed } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '@/utils/axios'; // Az Axios példányunk interceptorokkal
import { useAuthStore } from '@/stores/authStore';
// Telepítened kell ezt: npm install jwt-decode

const username = ref('');
const password = ref('');
const captchaImage = ref(null);    // A CAPTCHA kép Base64 stringje
const captchaAnswer = ref('');     // A felhasználó CAPTCHA válasza
const errorMessage = ref('');

const router = useRouter();
const authStore = useAuthStore();

const isFormValid = computed(() => {
  // 1. Felhasználónév és jelszó nem lehet üres
  const baseValid = username.value.trim() !== '' && password.value.trim() !== '';

  // 2. Ha a CAPTCHA kép látható, a válasz mező is ki kell, hogy legyen töltve
  if (captchaImage.value) {
    return baseValid && captchaAnswer.value.trim() !== '';
  }

  // 3. Ha nincs CAPTCHA, csak az alapfeltételek teljesülése kell
  return baseValid;
});

// --- 1. CAPTCHA Kép Kérése a Back-endről ---
const fetchCaptcha = async () => {
  try {
    // GET /api/auth/captcha végpont hívása
    const response = await apiClient.get('/auth/captcha');
    // A válasz egy Base64 string
    captchaImage.value = response.data;
    errorMessage.value = ''; // Töröljük a régi hibaüzenetet
  } catch (error) {
    console.error('Hiba a CAPTCHA kérésekor:', error);
  }
};

// --- 2. Bejelentkezési Logika ---
const handleLogin = async () => {
  errorMessage.value = '';

  const loginData = {
    username: username.value,
    password: password.value,
    captchaAnswer: captchaAnswer.value
  };

  try {
        const response = await apiClient.post('/auth/login', loginData);

        const token = response.data.token;
        const roles = response.data.roles;

        const userData = {
            username: response.data.username,
            name: response.data.name,
            id: response.data.id,
            lastLoginDate: response.data.lastLoginDate
        };

        // Pinia Store frissítése
        authStore.login(token, roles, userData);

        // Visszaállítás (sikeres bejelentkezés)
        captchaImage.value = null;

        // SIKER: átirányítás a Dashboardra
        router.push('/dashboard');

    } catch (error) {
        // HIBAKEZELÉS: Itt már csak a backend által küldött kódra figyelünk

        const status = error.response ? error.response.status : null;
        const data = error.response ? error.response.data : {};

        // A hibaüzenet a backend ErrorResponse DTO-ból érkezik (data.message)
        errorMessage.value = data.message || "Ismeretlen hiba történt a bejelentkezéskor.";

        // Ha a backend azt mondja: "CAPTCHA_REQUIRED", akkor kérjük a képet.
        if (status === 401 && data.code === 'CAPTCHA_REQUIRED') {
            await fetchCaptcha();
        }

        // Rossz felhasználónév/jelszó esetén a backend 401-et és 'LOGIN_FAILED'-et küld.
        // Ekkor csak az errorMessage jelenik meg, CAPTCHA még nem.
    }
};
</script>

<style scoped>
/* A design helyett csak a hibajelzés: */
.error-message {
  color: red;
  margin-top: 10px;
}
.captcha-image {
    border: 1px solid #ccc;
    margin-bottom: 10px;
    display: block;
}
.disabled-button {
  background-color: gray;
  cursor: not-allowed !important;
}

button:hover:not(.disabled-button){
  background-color: blue;
}


</style>
