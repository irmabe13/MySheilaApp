import {api} from "../lib/api";
import {setTokens} from "../lib/auth.ts";

export type TokenPair = { accessToken: string; refreshToken: string };
export type AuthRequest = { email: string; password: string };
export type RegisterRequest = { firstname: string; lastname: string; email: string; password: string };

export async function login(payload: AuthRequest): Promise<TokenPair> {
    const {data} = await api.post<TokenPair>("/auth/login", payload);
    setTokens(data.accessToken, data.refreshToken);
    return data;
}

export async function register(payload: RegisterRequest): Promise<void> {
    await api.post("/auth/register", payload);
}

export async function refresh(): Promise<string> {
    const rt = localStorage.getItem("refreshToken");
    if (!rt) throw new Error("Pas de refresh token");
    const {data} = await api.post<TokenPair>("/auth/refresh-token", {refreshToken: rt});
    setTokens(data.accessToken, data.refreshToken);
    return data.accessToken;
}

export async function logout(): Promise<void> {
    await api.post("/auth/logout");
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
}