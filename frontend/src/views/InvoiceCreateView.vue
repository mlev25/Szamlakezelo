<template>
  <div class="invoice-create-container">
    <h1>Új számla készítése</h1>
    <form @submit.prevent="handleCreateInvoice">

      <div class="form-group">
        <label for="customerName">Vásárló neve</label>
        <input type="text" id="customerName" v-model="invoice.customerName" required>
        <small v-if="customerNameError" class="validation-error">{{ customerNameError }}</small>
      </div>

      <div class="form-group date-group">
        <div class="date-field">
          <label for="issueDate">Kiállítás dátuma</label>
          <input type="date" id="issueDate" v-model="invoice.issueDate" required>
        </div>
        <div class="date-field">
          <label for="dueDate">Esedékesség dátuma</label>
          <input type="date" id="dueDate" v-model="invoice.dueDate" required>
          <small v-if="dueDateError" class="validation-error">{{ dueDateError }}</small>
        </div>
      </div>

      <div class="form-group">
        <label for="itemName">Tétel neve</label>
        <input type="text" id="itemName" v-model="invoice.itemName" required>
        <small v-if="itemNameError" class="validation-error">{{ itemNameError }}</small>
      </div>

      <div class="form-group">
        <label for="price">Ár</label>
        <input type="number" id="price" v-model.number="invoice.price" required min="1">
        <small v-if="priceError" class="validation-error">{{ priceError }}</small>
      </div>

      <div class="form-group">
        <label for="comment">Megjegyzés</label>
        <textarea id="comment" v-model="invoice.comment" required></textarea>
        <small v-if="commentError" class="validation-error">{{ commentError }}</small>
      </div>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <p v-if="successMessage" class="success-message">{{ successMessage }}</p>

      <div class="buttons">
        <button type="submit" :disabled="!isFormValid || isLoading">
          {{ isLoading ? 'Kiállítás...' : 'Számla kiállítása' }}
        </button>
        <button type="button" @click="router.back()" class="back-button">
          &larr;  Vissza
        </button>
      </div>

    </form>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
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


const minNameLength = 3;
const minCommentLength = 5;
const maxPrice = 99999999.99;

const customerNameError = computed(() => {
    if (invoice.value.customerName.trim().length === 0) return 'A vásárló neve kötelező.';
    if (invoice.value.customerName.trim().length < minNameLength) return `A név legalább ${minNameLength} karakter kell legyen.`;
    return '';
});

const itemNameError = computed(() => {
    if (invoice.value.itemName.trim().length === 0) return 'A tétel neve kötelező.';
    if (invoice.value.itemName.trim().length < minNameLength) return `A tételnév legalább ${minNameLength} karakter kell legyen.`;
    return '';
});

const commentError = computed(() => {
    if (invoice.value.comment.trim().length === 0) return 'A megjegyzés kötelező.';
    if (invoice.value.comment.trim().length < minCommentLength) return `A megjegyzés legalább ${minCommentLength} karakter kell legyen.`;
    return '';
});

const priceError = computed(() => {
    if (invoice.value.price <= 0) return 'Az ár nem lehet nulla vagy negatív.';
    if (invoice.value.price > maxPrice) return `Az ár túl magas (Maximum: ${maxPrice.toLocaleString('hu-HU')}).`;
    return '';
});

const dueDateError = computed(() => {
    if (!invoice.value.dueDate) return 'Az esedékesség dátuma kötelező.';

    const issue = new Date(invoice.value.issueDate);
    const due = new Date(invoice.value.dueDate);
    issue.setHours(0, 0, 0, 0);
    due.setHours(0, 0, 0, 0);

    if (due < issue) return 'Az esedékesség nem lehet korábbi, mint a kiállítás dátuma.';
    return '';
});

const isFormValid = computed(() => {
  return customerNameError.value === '' &&
         itemNameError.value === '' &&
         commentError.value === '' &&
         priceError.value === '' &&
         dueDateError.value === '';
});

const handleCreateInvoice = async () => {
  if (!isFormValid.value) return;
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
  margin: 20px auto;
  padding: 25px;
  background-color: #f5f5f5;
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
  background-color: #21d038;
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

.buttons{
  display: flex;
  flex-direction: row;
  align-items: end;
  justify-content: center;

}

.back-button{
  background-color: rgb(255, 0, 4);
}

.back-button:hover{
  background-color: rgb(255, 71, 71) !important;
}

.validation-error {
    color: #dc3545;
    font-size: 0.85em;
    margin-top: 5px;
}
</style>
