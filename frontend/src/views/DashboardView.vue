<template>
  <div class="dashboard-container">
    <h1>Üdvözöljük a Számlakezelő Rendszerben!</h1>

    <p class="user-info">
      Bejelentkezve mint: <b>{{ authStore.userData?.username || 'N/A' }}</b>
      (Szerepkörök: <b>{{ authStore.userRoles.join(', ') }}</b>)
      <span v-if="authStore.userData?.lastLoginDate"> | Utolsó belépés: <b>{{ formatDate(authStore.userData?.lastLoginDate) }}</b></span>
      <span v-else> | Utolsó belépés: Most/Ismeretlen</span>
    </p>

    <hr>

    <h2>Elérhető Funkciók</h2>

    <div class="feature-grid">

      <router-link to="/invoices" class="feature-card primary">
        <h3>Számlák Megtekintése</h3>
        <p>Az összes kiállított számla listája és kereső (Mindenki látja).</p>
      </router-link>

      <router-link
        v-if="hasRole('BOOKKEEPER') || hasRole('ADMIN')"
        to="/invoices/create"
        class="feature-card success">
        <h3>Új Számla Létrehozása</h3>
        <p>Új számla rögzítése a rendszerben (Könyvelő/Admin).</p>
      </router-link>

      <router-link
        v-if="hasRole('ADMIN')"
        to="/admin"
        class="feature-card danger">
        <h3>Adminisztrációs Panel</h3>
        <p>Felhasználói jogok és adatok kezelése (Admin).</p>
      </router-link>

    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';

const authStore = useAuthStore();

const hasRole = (role) => authStore.hasRole(role);

const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
  return new Date(dateString).toLocaleDateString('hu-HU', options);
};

</script>

<style scoped>

.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.user-info {
  margin-top: 10px;
  padding: 10px;
  background: rgb(220, 220, 232);
  border-left: 5px solid #007bff;
  border-radius: 4px;
  font-size: 1.1em;
}

hr {
    margin: 30px 0;
    border: 0;
    border-top: 1px solid #ccc;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 25px;
  margin-top: 30px;
}

.feature-card {
  display: block;
  padding: 25px;
  border-radius: 8px;
  text-decoration: none;
  color: #333;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
  height: 100%;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}

.feature-card h3 {
    margin-top: 0;
    margin-bottom: 10px;
}

.feature-card p {
    font-size: 0.9em;
    color: #555;
}

.primary {
  background-color: #e3f2fd;
  border: 1px solid #90caf9;
}

.success {
  background-color: #f8fca8;
  border: 1px solid #a5d6a7;
}

.danger { background-color: #ffebee;
  border: 1px solid #ef9a9a;
}

.info {
  background-color: #e0f7fa;
  border: 1px solid #80deea;
}

</style>
