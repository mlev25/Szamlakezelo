<template>
  <div id="app">
    <header v-if="isAuthenticated" class="app-header">
      <nav>
        <router-link to="/dashboard">Főoldal</router-link>
        <router-link to="/admin" v-if="hasRole('ADMIN')">Admin Panel</router-link>
        <button @click="handleLogout" class="logout-button">Kijelentkezés</button>
      </nav>
    </header>

    <main class="app-main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from './stores/authStore';

const authStore = useAuthStore();
const router = useRouter();

// Számított tulajdonságok a Store-ból
const isAuthenticated = computed(() => authStore.isAuthenticated);

// Getter hívása a jogosultság ellenőrzéséhez (pl. admin link megjelenítéséhez)
const hasRole = (role) => authStore.hasRole(role);

// Kijelentkezés metódus
const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style>
/* Ide jöhetnek a globális stílusok, vagy az assets/main.css-ben. */
.app-header {
  background: #333;
  padding: 15px;
  color: white;
  margin-bottom: 20px;
}
.app-header nav a {
  color: #fff;
  margin-right: 20px;
  text-decoration: none;
}
.app-header nav button {
  float: right;
  background: none;
  border: 1px solid white;
  color: white;
  cursor: pointer;
  padding: 5px 10px;
}
.app-main-content {
  padding: 20px;
}

/* ALAP RESET ÉS BETŰTÍPUS */
body {
    font-family: Arial, sans-serif;
    background-color: #f4f7f9;
    color: #333;
    margin: 0;
    padding: 0;
    line-height: 1.6;
}

#app {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
}

/* ŰRLAPOK ÁLTALÁNOS STÍLUSA */
.form-group {
    margin-bottom: 15px;
}

label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
}

input[type="text"],
input[type="password"] {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
}

button[type="submit"] {
    width: 100%;
    padding: 10px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
    margin-top: 10px;
}

/* HIBA ÜZENETEK */
.error-message {
    color: #d9534f;
    background-color: #f2dede;
    border: 1px solid #ebccd1;
    padding: 10px;
    border-radius: 4px;
    margin-top: 15px;
}

/* LOGIN ÉS REGISZTRÁCIÓ KONTÉNER */
.login-container, .register-container {
    max-width: 400px;
    margin: 50px auto;
    padding: 30px;
    background: white;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
}

/* APP.VUE SPECIFIKUS STÍLUSOK (Navigáció) */
.app-header {
  background: #333;
  padding: 15px 30px;
  color: white;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.app-header nav a {
  color: #fff;
  margin-right: 25px;
  text-decoration: none;
  font-weight: bold;
}
.app-header nav a:hover {
  text-decoration: underline;
}
.app-header nav a.router-link-active {
  color: #a4c5f9;
}
.app-header nav button {
  background: #dc3545;
  border: none;
  color: white;
  cursor: pointer;
  padding: 8px 15px;
  border-radius: 4px;
  font-weight: bold;
}
.app-main-content {
  flex-grow: 1;
  padding: 20px;
}
</style>
