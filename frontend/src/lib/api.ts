import axios from "axios";
import {API_URL} from "./config.ts";
import {clearTokens} from "./auth.ts"; // Ajout de l'import pour la déconnexion forcée

export const api = axios.create({
    baseURL: API_URL,
    headers: {"Content-Type": "application/json"},
});

// Intercepteur de Requête (pour ajouter le token d'accès)
api.interceptors.request.use((config) => {
    const token = localStorage.getItem("accessToken");
    if (token) config.headers.Authorization = `Bearer ${token}`.trim();
    return config;
});

// Intercepteur de Réponse (pour gérer l'expiration du token)
api.interceptors.response.use(
    (response) => response,
    (error) => {
        const status = error.response ? error.response.status : null;

        // Si le backend renvoie 401 (Non autorisé) ou 403 (Interdit), cela signifie
        // que le token est expiré, révoqué, ou invalide.
        if (status === 401 || status === 403) {

            // Loguer pour le débogage (optionnel)
            console.error(
                `[API Interceptor] Token invalide détecté (Status: ${status}). Déconnexion forcée.`
            );

            // 1. Suppression des tokens locaux pour forcer le ProtectedRoute à échouer.
            clearTokens();

            // 2. Redirection absolue vers la page de connexion.
            // (Utilisation de window.location.href pour assurer la déconnexion complète du contexte)
            window.location.href = "/login";
        }

        // Laisse passer les autres erreurs
        return Promise.reject(error);
    }
);
