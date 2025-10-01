export const getAccess = () => localStorage.getItem("accessToken");
export const getRefresh = () => localStorage.getItem("refreshToken");
export const setTokens = (a: string, r?: string) => {
    localStorage.setItem("accessToken", a);
    if (r) localStorage.setItem("refreshToken", r);
};
export const clearTokens = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
};
