<template>
  <div class="invoice-create-container">
    <h1>Új számla készítése</h1>
    <form @submit.prevent="handleCreateInvoice">

      <div class="form-group">
        <label for="customerName">Vásárló neve</label>
        <input type="text" id="customerName" v-model="invoice.customerName" required>
      </div>

      <div class="form-group date-group">
        <div class="date-field">
          <label for="issueDate">Kiállítás dátuma</label>
          <input type="date" id="issueDate" v-model="invoice.issueDate" required>
        </div>
        <div class="date-field">
          <label for="dueDate">Esedékesség dátuma</label>
          <input type="date" id="dueDate" v-model="invoice.dueDate" required>
        </div>
      </div>

      <div class="form-group">
        <label for="itemName">Tétel neve</label>
        <input type="text" id="itemName" v-model="invoice.itemName" required>
      </div>

      <div class="form-group">
        <label for="price">Ár</label>
        <input type="number" id="price" v-model.number="invoice.price" required min="1">
      </div>

      <div class="form-group">
        <label for="comment">Megjegyzés</label>
        <textarea id="comment" v-model="invoice.comment" required></textarea>
      </div>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <p v-if="successMessage" class="success-message">{{ successMessage }}</p>

      <button type="submit" :disabled="isLoading">
        {{ isLoading ? 'Kiállítás...' : 'Számla kiállítása' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '@/utils/axios';

const router = useRouter();

const invoice = ref({
  customerName: '',
  issueDate: new Date().toISOString().substring(0, 10),
  dueDate: '',
  itemName: '',
  comment: '',
  price: 0,
});

const errorMessage = ref('');
const successMessage = ref('');
const isLoading = ref(false);

const handleCreateInvoice = async () => {
  errorMessage.value = '';
  successMessage.value = '';
  isLoading.value = true;

  try {
    const response = await apiClient.post('/invoices/create', invoice.value);

    successMessage.value = `A(z) ${response.data.itemName} számla sikeresen kiállítva! Átirányítás számlákhoz...`;

    invoice.value.customerName = '';
    invoice.value.dueDate = '';
    invoice.value.itemName = '';
    invoice.value.comment = '';
    invoice.value.price = 0;

    setTimeout(() => {
      router.push('/invoices');
    }, 2000);

  } catch (error) {
    const message = error.response?.data?.message || 'Ismeretlen hiba a számla kiállításakor.';
    errorMessage.value = message;
    console.error("Számla kiállítási hiba:", error);

  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.invoice-create-container {
  max-width: 600px;
  margin: 30px auto;
  padding: 25px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #555;
}

.form-group input, .form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 16px;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.date-group {
    display: flex;
    gap: 20px;
}

.date-group .date-field {
    flex: 1;
}

button {
  width: 100%;
  padding: 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 18px;
  transition: background-color 0.3s;
}

button:hover:not(:disabled) {
  background-color: #0056b3;
}

button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
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
