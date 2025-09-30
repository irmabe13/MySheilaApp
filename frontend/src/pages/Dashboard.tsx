import {useEffect, useState} from "react";

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080/api";

export default function Dashboard() {
    const [me, setMe] = useState<any>(null);
    const [err, setErr] = useState<string | null>(null);

    useEffect(() => {
        const at = localStorage.getItem("accessToken");
        fetch(`${API_URL}/users/me`, {
            headers: {Authorization: at ? `Bearer ${at}` : ""}
        })
            .then(async (r) => {
                if (!r.ok) throw new Error(await r.text());
                return r.json();
            })
            .then(setMe)
            .catch((e) => setErr(e.message));
    }, []);

    function logout() {
        const at = localStorage.getItem("accessToken");
        fetch(`${API_URL}/auth/logout`, {method: "POST", headers: {Authorization: `Bearer ${at}`}})
            .finally(() => {
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                window.location.href = "/login";
            });
    }

    return (
        <div className="max-w-lg mx-auto p-6">
            <h1 className="text-xl font-semibold mb-4">Dashboard (test)</h1>
            {err && <p className="text-red-600 text-sm mb-2">{err}</p>}
            <pre className="text-xs bg-gray-100 p-3 rounded">{JSON.stringify(me, null, 2)}</pre>
            <button onClick={logout} className="mt-4 px-3 py-2 rounded bg-black text-white">Se déconnecter</button>
        </div>
    );
}