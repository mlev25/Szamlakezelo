import axios from "axios";
import router from "@/router";
import { useAuthStore } from "@/stores/authStore";

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: true
});

apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    const token = authStore.token;

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    const authStore = useAuthStore();

    if (error.response && (error.response.status === 401 || error.response.status === 403)){
      authStore.logout();
      router.push('/login');
    }

    return Promise.reject(error);
  }
);

export default apiClient;
