<template>
  <div class="register-container">
    <h1>Új Fiók Regisztrációja</h1>

    <form @submit.prevent="handleRegister">

      <div class="form-group">
        <label for="name">Teljes Név</label>
        <input type="text" id="name" v-model="name" required>
        <small v-if="name.length === 0 && submitted" class="validation-error">A név mező kötelező.</small>
      </div>

      <div class="form-group">
        <label for="username">Felhasználónév</label>
        <input type="text" id="username" v-model="username" required minlength="4">
        <small v-if="username.length > 0 && username.length < 4" class="validation-error">A felhasználónév legalább 4 karakter legyen.</small>
      </div>

      <div class="form-group">
        <label for="password">Jelszó</label>
        <input type="password" id="password" v-model="password" required minlength="8">
        <small v-if="password.length > 0 && password.length < 6" class="validation-error">A jelszónak legalább 6 karakter hosszúnak kell lennie.</small>
        <small v-if="password.length >= 8 && !isPasswordStrong" class="validation-error">A jelszónak tartalmaznia kell kisbetűt, nagybetűt és számot is.</small>
      </div>

      <div class="form-group">
        <label for="role">Szerepkör</label>
        <select id="role" v-model="role" required>
          <option value="USER">Felhasználó</option>
          <option value="BOOKKEEPER">Könyvelő</option>
        </select>
      </div>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <p v-if="successMessage" class="success-message">{{ successMessage }}</p>

      <button
        type="submit"
        :disabled="!isFormValid"
        :class="{ 'disabled-button': !isFormValid }"
      >
        {{ isLoading ? 'Regisztráció...' : 'Regisztrálok' }}
      </button>
    </form>

    <router-link to="/login">Már van fiókja? Jelentkezzen be!</router-link>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '@/utils/axios';

const name = ref('');
const username = ref('');
const password = ref('');
const role = ref('USER');
const errorMessage = ref('');
const successMessage = ref('');
const isLoading = ref(false);
const submitted = ref(false);

const router = useRouter();


const isPasswordStrong = computed(() => {
    if (password.value.length < 6) return false;
    const strongRegex = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])");
    return strongRegex.test(password.value);
});

const isFormValid = computed(() => {
    return (
        name.value.trim().length > 0 &&
        username.value.trim().length >= 4 &&
        password.value.trim().length >= 8 &&
        isPasswordStrong.value
    );
});


const handleRegister = async () => {
  submitted.value = true;

  if (!isFormValid.value) return;

  errorMessage.value = '';
  successMessage.value = '';
  isLoading.value = true;

  const registerData = {
    name: name.value,
    username: username.value,
    password: password.value,
    role: role.value
  };

  try {
    await apiClient.post('/auth/register', registerData);

    successMessage.value = `Sikeres regisztráció, ${username.value}! Jelentkezzen be!`;

    name.value = '';
    username.value = '';
    password.value = '';
    submitted.value = false;

    setTimeout(() => {
        router.push('/login');
    }, 2000);

  } catch (error) {
    const data = error.response ? error.response.data : {};

    errorMessage.value = data.message || "Hiba történt a regisztráció során. Kérjük, próbálja újra.";
    console.error("Regisztrációs hiba:", error);

  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 50px auto;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.form-group {
  margin-bottom: 15px;
}
.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input, .form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

button {
    padding: 10px 20px;
    background-color: #28a745;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
    width: 100%;
}

.disabled-button {
    background-color: #cccccc !important;
    cursor: not-allowed !important;
    opacity: 0.6;
}

button:hover:not(.disabled-button) {
    background-color: #1e7e34;
}

.validation-error { color: #dc3545;
  font-size: 0.85em;
  display: block;
  margin-top: 5px;
}

.error-message {
  color: #dc3545;
  font-weight: bold;
  margin-top: 15px;
}

.success-message {
  color: #28a745;
  font-weight: bold;
  margin-top: 15px;
}
</style>
