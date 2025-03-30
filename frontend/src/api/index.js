import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api", // Spring Boot API 기본 URL
  withCredentials: true,
});

export default api;
