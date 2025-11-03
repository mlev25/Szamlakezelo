<template>
  <div class="invoice-list-container">
    <h1>Számlák listája</h1>

    <router-link
      v-if="hasRole('BOOKKEEPER') || hasRole('ADMIN')"
      to="/invoices/create"
      class="create-button">
      Új Számla Létrehozása
    </router-link>

    <p v-if="isLoading" class="loading-message">Számlák betöltése...</p>
    <p v-else-if="error" class="error-message">{{ error }}</p>
    <p v-else-if="invoices.length === 0" class="no-data-message">Nincs megjeleníthető számla.</p>

    <table v-else class="invoice-table">
      <thead>
        <tr>
          <th>Vásárló Neve</th>
          <th>Tétel Neve</th>
          <th>Kiállítás Dátuma</th>
          <th>Esedékesség Dátuma</th>
          <th>Ár (HUF)</th>
          <th>...</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="invoice in invoices" :key="invoice.id" @click="viewInvoice(invoice.id)" class="clickable-row">
          <td>{{ invoice.customerName }}</td>
          <td>{{ invoice.itemName }}</td>
          <td>{{ formatDate(invoice.issueDate) }}</td>
          <td>{{ formatDate(invoice.dueDate) }}</td>
          <td class="amount">{{ formatCurrency(invoice.price) }}</td>
          <td>></td>
        </tr>
      </tbody>
    </table>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '@/utils/axios';
import { useAuthStore } from '@/stores/authStore';

const invoices = ref([]);
const isLoading = ref(false);
const error = ref(null);

const router = useRouter();
const authStore = useAuthStore();
const hasRole = (role) => authStore.hasRole(role);

const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  return new Date(dateString).toLocaleDateString('hu-HU');
};

const formatCurrency = (amount) => {
  if (amount === undefined || amount === null) return 'N/A';
  return new Intl.NumberFormat('hu-HU', { style: 'currency', currency: 'HUF' }).format(amount);
};

const fetchInvoices = async () => {
  isLoading.value = true;
  error.value = null;

  try {
    const response = await apiClient.get('/invoices/all');
    invoices.value = response.data;
  } catch (err) {
    error.value = 'Hiba történt a számlák lekérésekor.';
    console.error('API Error:', err);
  } finally {
    isLoading.value = false;
  }
};

const viewInvoice = (id) => {
  router.push(`/invoices/${id}`);
};

onMounted(() => {
  fetchInvoices();
});
</script>

<style scoped>
.invoice-list-container { max-width: 1200px; margin: 0 auto; padding: 20px; }
.create-button {
    display: inline-block;
    padding: 10px 15px;
    background-color: #28a745;
    color: white;
    text-decoration: none;
    border-radius: 5px;
    margin-bottom: 20px;
    transition: background-color 0.3s;
}
.create-button:hover {
  background-color: #1e7e34;
}
.invoice-table {
  width: 100%;
  border-collapse: collapse;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.invoice-table th, .invoice-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}
.invoice-table th { background-color: #f8f8f8;
  font-weight: bold;
  color: #333;
}
.clickable-row { cursor: pointer;
  transition: background-color 0.2s;
}
.clickable-row:hover {
  background-color: #f1f1f1;
}
.amount {
  font-weight: bold;
}
.loading-message, .error-message, .no-data-message {
  margin-top: 20px;
  text-align: center;
  font-size: 1.1em;
}
.error-message {
  color: #dc3545;
}
</style>
