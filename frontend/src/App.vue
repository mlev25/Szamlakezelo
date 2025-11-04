<template>
  <div id="app">
    <header v-if="isAuthenticated" class="app-header">
      <nav>
        <router-link to="/dashboard">Főoldal</router-link>
        <router-link to="/invoices">Számlák</router-link>
        <router-link to="/admin" v-if="hasRole('ADMIN')">Admin Panel</router-link>
      </nav>
      <button @click="handleLogout" class="logout-button">Kilépés</button>
    </header>

    <main class="app-main-content">
      <router-view />
    </main>

    <footer class="app-footer">
      <span>Source Code: <a class="source-link" href="https://github.com/mlev25/Szamlakezelo">mlev25</a></span>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from './stores/authStore';

const authStore = useAuthStore();
const router = useRouter();

const isAuthenticated = computed(() => authStore.isAuthenticated);

const hasRole = (role) => authStore.hasRole(role);

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style>
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

body {
  font-family: Arial, sans-serif;
  background: #68c3c4;
  background: linear-gradient(6deg, rgba(104, 195, 196, 1) 0%, rgba(240, 224, 153, 1) 100%);
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

.error-message {
    color: #d9534f;
    background-color: #f2dede;
    border: 1px solid #ebccd1;
    padding: 10px;
    border-radius: 4px;
    margin-top: 15px;
}

.login-container, .register-container {
    max-width: 400px;
    margin: 90px auto;
    padding: 30px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
}

.app-header, .app-footer{
  background-color: #000000be;
  padding: 15px 30px;
  color: white;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 0.5rem;
  margin: 0.1rem;
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

.app-header .logout-button {
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
.app-footer{
  display: block;
  text-align: center;
}
.source-link{
  text-decoration: none;
  color: blue;
}

.source-link:hover{
  color: lightblue;
}
</style>
