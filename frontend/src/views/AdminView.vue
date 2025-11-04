<template>
  <div class="admin-container">
    <h1>Adminisztrációs Panel</h1>
    <p class="role-info">Csak Adminisztrátorok számára elérhető tartalom.</p>

    <h2>Felhasználók Kezelése</h2>

    <p v-if="isLoading" class="loading-message">Felhasználói adatok betöltése...</p>
    <p v-else-if="error" class="error-message">{{ error }}</p>

    <div v-else class="user-table-wrapper">
      <p v-if="successMessage" class="success-message">{{ successMessage }}</p>

      <table class="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Felhasználónév</th>
            <th>Teljes Név</th>
            <th>Utolsó Belépés</th>
            <th>Szerepkörök</th>
            <th>Törlés</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.name }}</td>
            <td>{{ formatDate(user.lastLoginDate) }}</td>

            <td :class="{ 'editing-cell': isEditing(user.id) }">

              <div v-if="!isEditing(user.id)" @click="startEditing(user)" class="role-display-area">
                <span
                    v-for="role in user.roles"
                    :key="role"
                    :class="['role-tag', role.toLowerCase()]"
                >
                  {{ role }}
                </span>
                <span class="edit-hint">(Szerkesztés)</span>
              </div>

              <div v-else class="role-edit-controls">

                <div class="role-checkbox-group">
                  <div v-for="role in availableRoles" :key="role" class="checkbox-item">
                    <input
                      type="checkbox"
                      :id="`role-${user.id}-${role}`"
                      :value="role"
                      v-model="editingRoles[user.id].roles"
                    >
                    <label :for="`role-${user.id}-${role}`">{{ role }}</label>
                  </div>
                </div>
                <div class="edit-actions">
                  <button
                      @click="saveRoles(user.id)"
                      :disabled="isSaving"
                      class="action-button save"
                  >
                    Mentés
                  </button>
                  <button
                      @click="cancelEditing(user.id)"
                      class="action-button cancel"
                  >
                    Mégse
                  </button>
                </div>
                <p v-if="editingRoles[user.id].error" class="inline-error">{{ editingRoles[user.id].error }}</p>
              </div>
            </td>

            <td>
              <button
                @click="deleteUser(user.id, user.username)"
                class="action-button delete"
                :disabled="isSelf(user.id)"
              >
                Törlés
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-if="users.length === 0" class="no-data">Nincs megjeleníthető felhasználói adat.</p>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import apiClient from '@/utils/axios';
import { useAuthStore } from '@/stores/authStore';

const authStore = useAuthStore();

const users = ref([]);
const isLoading = ref(false);
const error = ref(null);
const successMessage = ref(null);
const isSaving = ref(false);

const currentlyEditingId = ref(null);
const editingRoles = ref({});
const availableRoles = ['ADMIN', 'BOOKKEEPER', 'USER'];

const fetchUsers = async () => {
  isLoading.value = true;
  error.value = null;
  successMessage.value = null;
  try {
    const response = await apiClient.get('/admin/users');
    users.value = response.data;
  } catch (err) {
    const message = err.response?.data?.message || 'Hiba a felhasználók lekérésekor. Nincs jogosultsága.';
    error.value = message;
    console.error("Admin API hiba:", err);
  } finally {
    isLoading.value = false;
  }
};


const isEditing = (userId) => currentlyEditingId.value === userId;

const isSelf = (userId) => authStore.userData?.id === userId;

const startEditing = (user) => {
    if (isSelf(user.id)) return;

    currentlyEditingId.value = user.id;
    editingRoles.value[user.id] = {
        roles: [...user.roles],
        error: null
    };
};

const cancelEditing = (userId) => {
    currentlyEditingId.value = null;
    delete editingRoles.value[userId];
};

const saveRoles = async (userId) => {
    isSaving.value = true;
    const userEditState = editingRoles.value[userId];
    userEditState.error = null;
    successMessage.value = null;

    if (userEditState.roles.length === 0) {
        userEditState.error = 'Legalább egy szerepkör kötelező!';
        isSaving.value = false;
        return;
    }

    try {
        const payload = { roles: userEditState.roles };
        const response = await apiClient.put(`/admin/users/${userId}/roles`, payload);

        successMessage.value = `Szerepkörök sikeresen mentve: ${response.data.username}.`;

        const index = users.value.findIndex(u => u.id === userId);
        if (index !== -1) {
            users.value[index] = response.data;
        }

        cancelEditing(userId);

        setTimeout(() => { successMessage.value = null; }, 3000);

    } catch (err) {
        const message = err.response?.data?.message || 'Hiba a szerepkörök mentésekor.';
        userEditState.error = message;
        console.error("Szerepkör mentési hiba:", err);

    } finally {
        isSaving.value = false;
    }
}

const deleteUser = async (userId, username) => {
    if (isSelf(userId)) return;

    if (confirm(`Biztosan törölni szeretné a(z) ${username} nevű felhasználót?`)) {
        try {
            await apiClient.delete(`/admin/users/${userId}`);

            users.value = users.value.filter(user => user.id !== userId);
            successMessage.value = `A(z) ${username} nevű felhasználó sikeresen törölve.`;

        } catch (err) {
            const message = err.response?.data?.message || 'Hiba történt a felhasználó törlésekor.';
            error.value = message;
            console.error("Törlési hiba:", err);
        }
    }
}


const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString('hu-HU', options);
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.admin-container{
  background-color: rgb(240, 234, 204);
  padding: 20px;
  border-radius: 1rem;
  border: 3px solid #c1c5c8;
}

.user-table{
  background-color: rgb(240, 227, 227);
  padding: 10px;
  border: 3px solid rgb(101, 100, 100);
  border-radius: 1rem;
}


.user-table th, .user-table td {
  vertical-align: top;
  padding: 5px 20px;
  text-align: left;
}

.role-display-area {
    cursor: pointer;
    min-height: 40px;
    padding: 5px;
    border: 1px dashed transparent;
    transition: border-color 0.2s;
}
.role-display-area:hover {
    border-color: #007bff;
}
.role-tag {
    display: inline-block;
    padding: 3px 8px;
    margin: 2px;
    border-radius: 12px;
    font-size: 0.8em;
    font-weight: bold;
    color: white;
}
.role-tag.admin { background-color: #dc3545; }
.role-tag.bookkeeper { background-color: #ffc107; color: #333;}
.role-tag.user { background-color: #007bff; }

.edit-hint {
    display: block;
    font-size: 0.75em;
    color: #888;
    margin-top: 5px;
}

.role-edit-controls {
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding: 5px 0;
}

.role-multiselect {
    width: 100%;
    border: 1px solid #007bff;
    padding: 5px;
}

.edit-actions {
    display: flex;
    gap: 8px;
}

.action-button {
    padding: 8px 12px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 0.9em;
    transition: background-color 0.2s;
}
.action-button.delete {
    background-color: #dc3545;
    color: white;
    margin-left: 10px;
}
.action-button.save {
    background-color: #28a745;
    color: white;
}
.action-button.cancel {
    background-color: #6c757d;
    color: white;
}

.inline-error {
    color: #dc3545;
    font-size: 0.8em;
    margin: 0;
}

.success-message {
    padding: 10px;
    background-color: #d4edda;
    color: #155724;
    border: 1px solid #c3e6cb;
    border-radius: 4px;
    margin-bottom: 20px;
    text-align: center;
    font-weight: bold;
}

.role-checkbox-group {
  border: 1px solid #007bff;
  border-radius: 4px;
  padding: 10px;
}

.checkbox-item {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.checkbox-item label {
  margin-left: 8px;
  font-size: 0.9em;
}

.checkbox-item input[type="checkbox"] {
  width: auto;
}
</style>
