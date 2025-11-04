<template>
  <div class="login-container">
    <h2>Bejelentkezés</h2>

    <form @submit.prevent="handleLogin">

      <div class="form-group">
        <label for="username">Felhasználónév</label>
        <input type="text" id="username" v-model="username" required placeholder="SzepFelhasznalo25">
      </div>

      <div class="form-group">
        <label for="password">Jelszó</label>
        <input type="password" id="password" v-model="password" required placeholder="ErosJelszo11">
      </div>

      <div v-if="captchaImage" class="captcha-group">
        <label>CAPTCHA Ellenőrzés</label>
        <img :src="'data:image/jpeg;base64,' + captchaImage" alt="CAPTCHA kép a bejelentkezéshez" class="captcha-image">
        <input type="text" v-model="captchaAnswer" placeholder="Írja be a fenti kódot" required>
      </div>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

      <button type="submit" :disabled="!isFormValid" :class="{ 'disabled-button': !isFormValid }">Belépés</button>
    </form>

    <router-link to="/register" class="reg-link">Nincs még fiókja? Regisztráljon!</router-link>
  </div>
</template>

<script setup>
import { ref,computed } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '@/utils/axios';
import { useAuthStore } from '@/stores/authStore';

const username = ref('');
const password = ref('');
const captchaImage = ref(null);
const captchaAnswer = ref('');
const errorMessage = ref('');

const router = useRouter();
const authStore = useAuthStore();

const isFormValid = computed(() => {
  const baseValid = username.value.trim() !== '' && password.value.trim() !== '';

  if (captchaImage.value) {
    return baseValid && captchaAnswer.value.trim() !== '';
  }

  return baseValid;
});

const fetchCaptcha = async () => {
  try {
    const response = await apiClient.get('/auth/captcha');
    captchaImage.value = response.data;
    errorMessage.value = '';
  } catch (error) {
    console.error('Hiba a CAPTCHA kérésekor:', error);
  }
};

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

        authStore.login(token, roles, userData);

        captchaImage.value = null;

        router.push('/dashboard');

    } catch (error) {
        const status = error.response ? error.response.status : null;
        const data = error.response ? error.response.data : {};

        errorMessage.value = data.message || "Ismeretlen hiba történt a bejelentkezéskor.";

        if (status === 401 && data.code === 'CAPTCHA_REQUIRED') {
            await fetchCaptcha();
        }
    }
};
</script>

<style scoped>
.login-container{
  background-color: lightgray;
}

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

.reg-link{
  text-decoration: none;
  color: darkcyan;
  padding: 2px;
  border-radius: 0.3rem;
}

.reg-link:hover{
  background-color: lightblue;
}

</style>
