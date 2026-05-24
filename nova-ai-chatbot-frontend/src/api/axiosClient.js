import axios from "axios";

const axiosClient = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: true,
    timeout: 10000,
});

axiosClient.interceptors.response.use(
    (response) => response,
    async (error) => {
        if (error.response?.status === 401) {
            // Clear any stale state
            window.location.href = "http://localhost:8080/oauth2/authorization/google";
        }
        return Promise.reject(error);
    }
);

export default axiosClient;