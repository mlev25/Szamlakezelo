import { defineStore } from "pinia";

const TOKEN_KEY = 'authToken';
const USER_ROLES_KEY = 'userRoles';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || null,
    userRoles: JSON.parse(localStorage.getItem(USER_ROLES_KEY) || '[]'),
    userData: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,

    hasRole: (state) => (roleName) => {
      const role = 'ROLE_' + roleName.toUpperCase();
      return state.userRoles.includes(role);
    }
  },

  actions: {
    login (token, roles, userData) {
      this.token = token;
      this.userRoles = roles;
      this.userData = userData;

      localStorage.setItem(TOKEN_KEY, token);
      localStorage.setItem(USER_ROLES_KEY, JSON.stringify(roles));
    },

    logout() {
      this.token = null;
      this.userRoles = [];
      this.userData = null;

      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_ROLES_KEY);
    }
  }
})
