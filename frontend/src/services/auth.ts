import {http} from "./http";

export type TokenPair = { accessToken: string; refreshToken: string };
export type AuthRequest = { email: string; password: string };
export type RegisterRequest = { firstname: string; lastname: string; email: string; password: string };

export async function login(payload: AuthRequest): Promise<TokenPair> {
    const {data} = await http.post<TokenPair>("/auth/login", payload);
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);
    return data;
}

export async function register(payload: RegisterRequest): Promise<void> {
    await http.post("/auth/register", payload);
}

export async function refresh(): Promise<string> {
    const rt = localStorage.getItem("refreshToken");
    if (!rt) throw new Error("Pas de refresh token");
    const {data} = await http.post<TokenPair>("/auth/refresh-token", {refreshToken: rt});
    localStorage.setItem("accessToken", data.accessToken);
    return data.accessToken;
}

export async function logout(): Promise<void> {
    await http.post("/auth/logout");
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
}