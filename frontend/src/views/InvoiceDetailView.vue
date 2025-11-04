<template>
  <div class="invoice-detail-container">
    <button @click="router.back()" class="back-button">
      &larr;  Vissza
    </button>

    <h1>Számla részletes megtekintése</h1>

    <p v-if="isLoading" class="loading-message">Számla adatok betöltése...</p>
    <p v-else-if="error" class="error-message">{{ error }}</p>

    <form v-else class="read-only-form">

      <div class="form-group">
        <label>Számla azonosító</label>
        <input type="text" :value="invoice.id" readonly disabled>
      </div>

      <div class="form-group">
        <label>Vásárló Neve</label>
        <input type="text" :value="invoice.customerName" readonly disabled>
      </div>

      <div class="form-group-row">
          <div class="form-group">
            <label>Kiállítás Dátuma</label>
            <input type="text" :value="formatDate(invoice.issueDate)" readonly disabled>
          </div>
          <div class="form-group">
            <label>Esedékesség Dátuma</label>
            <input type="text" :value="formatDate(invoice.dueDate)" readonly disabled>
          </div>
      </div>

      <div class="form-group">
        <label>Tétel Neve</label>
        <input type="text" :value="invoice.itemName" readonly disabled>
      </div>

      <div class="form-group">
        <label>Ár</label>
        <input type="text" :value="formatCurrency(invoice.price)" readonly disabled>
      </div>

      <div class="form-group">
        <label>Megjegyzés</label>
        <textarea :value="invoice.comment" rows="3" readonly></textarea>
      </div>
    </form>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import apiClient from '@/utils/axios';

const route = useRoute();
const router = useRouter();
const invoice = ref(null);
const isLoading = ref(true);
const error = ref(null);

const invoiceId = route.params.id;

const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  return new Date(dateString).toLocaleDateString('hu-HU');
};

const formatCurrency = (amount) => {
  if (amount === undefined || amount === null) return 'N/A';
  return new Intl.NumberFormat('hu-HU', { style: 'currency', currency: 'HUF' }).format(amount);
};

const fetchInvoice = async () => {
  isLoading.value = true;
  error.value = null;

  try {
    const response = await apiClient.get(`/invoices/${invoiceId}`);
    invoice.value = response.data;
  } catch (err) {
    error.value = 'Hiba történt a számla részleteinek lekérésekor. A számla nem létezik vagy nincs jogosultsága megtekinteni.';
    console.error('API Error:', err);
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchInvoice();
});
</script>

<style scoped>
.invoice-detail-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 30px;
  background: #ebeaea;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.back-button {
    background: #30b959;
    color: white;
    border: none;
    padding: 10px 15px;
    border-radius: 4px;
    cursor: pointer;
    margin-bottom: 25px;
    transition: background-color 0.3s;
}
.back-button:hover {
    background: #5a6268;
}

h1 {
    border-bottom: 2px solid #eee;
    padding-bottom: 10px;
    margin-bottom: 25px;
    font-size: 1.8em;
}

.read-only-form .form-group {
    margin-bottom: 20px;
}
.read-only-form label {
    display: block;
    font-weight: bold;
    margin-bottom: 5px;
    color: #555;
}
.read-only-form input, .read-only-form textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    background-color: #f8f8f8;
    color: #333;
    font-size: 1em;
    box-sizing: border-box;
}

.read-only-form textarea {
    resize: none;
}

.form-group-row {
    display: flex;
    gap: 20px;
}
.form-group-row .form-group {
    flex: 1;
}

.loading-message, .error-message {
    margin-top: 20px;
    text-align: center;
    font-size: 1.1em;
}
.error-message { color: #dc3545; }
</style>
