import { useMemo, useState, type FormEvent, type ComponentProps } from "react";
import logo from "../assets/logo.png";
import { useNavigate } from "react-router-dom";

const API_URL =
  (import.meta as any)?.env?.VITE_API_URL ??
  "http://localhost:8080/api";


function AppPresentation() {
    const DAYS = ["Dim","Lun","Mar","Mer","Jeu","Ven","Sam"] as const;

    // Canevas & grille (08:00 → 22:00)
    const W = 1000, H = 480;
    const gridX = 100, gridY = 160, gridW = 720, gridH = 220;
    const colW = gridW / DAYS.length;
    const dayY = 120;
    const centers = useMemo(() => DAYS.map((_, i) => gridX + colW * (i + 0.5)), [colW]);
    const timeToY = (h: number, m = 0) => gridY + (((h + m/60) - 8) / 14) * gridH;

    // Bandes de disponibilités (exemples illustratifs)
    const availBands = [
        { label: "Disponibilités matin",       start: timeToY(9),  end: timeToY(12), color: "var(--ms-primary)"   },
        { label: "Disponibilités après-midi",  start: timeToY(14), end: timeToY(16), color: "var(--ms-secondary)" },
    ];

    // Éléments illustratifs dans la grille (pas des events réels)
    const chips = [
        { day: 1, y: timeToY(9, 0),  w: 136, label: "Tâche planifiée", color: "var(--ms-primary)"   }, // Lun
        { day: 3, y: timeToY(14, 0), w: 120, label: "Rendez-vous",     color: "var(--ms-secondary)" }, // Mer
        { day: 4, y: timeToY(18, 0), w: 128, label: "Pause",           color: "var(--ms-peach)"     }, // Jeu
    ];

    // Callouts très courts (compris d’un coup d’œil)
    const callouts = [
        { text: "Définis tes disponibilités.",
            ax: gridX + 16, ay: timeToY(9) + 6, x: 60, y: 46, w: 360, h: 44 },
        { text: "Choisis tes objectifs.",
            ax: gridX + colW * 3 + 18, ay: timeToY(14) + 8, x: 560, y: 50, w: 360, h: 44 },
        { text: "Priorités de la semaine : visibles en un coup d’œil.",
            ax: gridX + gridW - 40, ay: gridY + gridH - 18, x: 560, y: 420, w: 360, h: 44 },
    ];

    return (
        <section aria-label="Présentation My Sheïla — Planning compréhensible" className="relative h-full overflow-hidden">
            {/* Fond coloré (violet + secondaires) — pleine colonne, sans encadré */}
            <div
                aria-hidden
                className="absolute inset-0"
                style={{
                    background: `
            radial-gradient(1200px 800px at -20% -20%, color-mix(in oklab, var(--ms-primary) 22%, transparent) 0%, transparent 60%),
            radial-gradient(900px 700px at 110% 10%, color-mix(in oklab, var(--ms-secondary) 18%, transparent) 0%, transparent 70%),
            radial-gradient(700px 600px at 50% 100%, color-mix(in oklab, var(--ms-peach) 14%, transparent) 0%, transparent 75%),
            linear-gradient(180deg, #ffffff 0%, #faf7ff 100%)
          `,
                }}
            />
            <div aria-hidden className="pointer-events-none absolute inset-0 opacity-[0.04]"
                 style={{ backgroundImage: "radial-gradient(#000 0.6px, transparent 0.6px)", backgroundSize: "14px 14px" }} />

            {/* Titre & légende courte */}
            <div className="relative px-6 pt-8">
                <h2 className="mt-3 text-xl font-semibold text-[color:var(--ms-ink)]">
                    MySheïla transforme tes routines en un planning clair et motivant — sans friction, sans bruit.
                </h2>
                <p className="mt-1 text-sm text-[color:var(--ms-ink-soft)]">
                    Planifie moins. Vis plus.
                </p>
            </div>

            {/* Illustration principale */}
            <svg viewBox={`0 0 ${W} ${H}`} className="relative block h-auto w-full">
                <defs>
                    <filter id="soft" x="-50%" y="-50%" width="200%" height="200%">
                        <feGaussianBlur in="SourceAlpha" stdDeviation="3" />
                        <feOffset dx="0" dy="1.5" />
                        <feComponentTransfer><feFuncA type="linear" slope="0.18" /></feComponentTransfer>
                        <feMerge><feMergeNode/><feMergeNode in="SourceGraphic"/></feMerge>
                    </filter>
                    <clipPath id="gridClip"><rect x={gridX} y={gridY} width={gridW} height={gridH} rx="12" /></clipPath>
                    <linearGradient id="panel" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0" stopColor="#ffffff" stopOpacity="0.96"/><stop offset="1" stopColor="#ffffff" stopOpacity="0.90"/>
                    </linearGradient>
                </defs>


                {/* Grille semaine */}
                <g filter="url(#soft)">
                    <rect x={gridX} y={gridY} width={gridW} height={gridH} rx="12" fill="url(#panel)" stroke="color-mix(in oklab, var(--ms-primary) 18%, #E5E7EB)" />
                    {Array.from({ length: DAYS.length - 1 }).map((_, i) => (
                        <line key={i} x1={gridX + colW * (i + 1)} y1={gridY} x2={gridX + colW * (i + 1)} y2={gridY + gridH} stroke="color-mix(in oklab, var(--ms-primary) 12%, #E5E7EB)" />
                    ))}
                    {Array.from({ length: 4 }).map((_, i) => (
                        <line key={i} x1={gridX} y1={gridY + ((i + 1) * gridH) / 5} x2={gridX + gridW} y2={gridY + ((i + 1) * gridH) / 5} stroke="color-mix(in oklab, var(--ms-secondary) 10%, #E5E7EB)" />
                    ))}
                </g>

                {/* Bandes de disponibilités (toute largeur, clipées) */}
                <g clipPath="url(#gridClip)">
                    {availBands.map((b, idx) => {
                        const y = b.start, h = Math.max(6, b.end - b.start);
                        return (
                            <g key={idx} opacity="0.9">
                                <rect x={gridX} y={y} width={gridW} height={h} rx={6} fill={b.color} opacity="0.10" />
                                <rect x={gridX} y={y} width={gridW} height={1} fill={b.color} opacity="0.18" />
                                <rect x={gridX} y={y + h - 1} width={gridW} height={1} fill={b.color} opacity="0.18" />
                            </g>
                        );
                    })}
                </g>

                {/* Labels jours */}
                {centers.map((cx, i) => (
                    <text key={DAYS[i]} x={cx} y={dayY} textAnchor="middle" fontSize="12" fill="var(--ms-ink-soft)" style={{ fontWeight: 600 }}>
                        {DAYS[i]}
                    </text>
                ))}

                {/* Éléments illustratifs (chips) */}
                <g clipPath="url(#gridClip)" filter="url(#soft)">
                    {chips.map((c, idx) => {
                        const x = gridX + colW * c.day + 12; const h = 22;
                        return (
                            <g key={idx}>
                                <rect x={x - 6} y={c.y - 8} width={c.w + 12} height={h + 6} rx={12} fill={c.color} opacity="0.08" />
                                <rect x={x} y={c.y} width={c.w} height={h} rx={11} fill="#fff" opacity="0.98" />
                                <circle cx={x + 12} cy={c.y + 11} r={4} fill={c.color} />
                                <text x={x + 26} y={c.y + 15} fontSize="11" fill="var(--ms-ink)" fontWeight="600">{c.label}</text>
                            </g>
                        );
                    })}
                </g>

                {/* Callouts (explication courte, sans IA) */}
                {callouts.map((c, i) => (
                    <g key={`co-${i}`}>
                        <line x1={c.x + c.w/2} y1={c.y + c.h} x2={c.ax} y2={c.ay}
                              stroke="color-mix(in oklab, var(--ms-primary) 35%, #6B7280)" strokeWidth="1.5" strokeDasharray="4 6" />
                        <circle cx={c.ax} cy={c.ay} r="3" fill="#fff"
                                stroke="color-mix(in oklab, var(--ms-primary) 35%, #6B7280)" strokeWidth="1" />
                        <rect x={c.x} y={c.y} width={c.w} height={c.h} rx="10" fill="#fff" opacity="0.98" />
                        <text x={c.x + 12} y={c.y + 20} fontSize="11" fill="var(--ms-ink)">{c.text}</text>
                    </g>
                ))}

                {/* Timeline à gauche */}
                <g>
                    <line x1={gridX - 28} y1={gridY + 8} x2={gridX - 28} y2={gridY + gridH - 8}
                          stroke="color-mix(in oklab, var(--ms-primary) 40%, #374151)" strokeWidth="1.5" strokeDasharray="4 6" opacity="0.75" />
                    <circle cx={gridX - 28} cy={gridY + 8} r="3" fill="#fff" stroke="color-mix(in oklab, var(--ms-primary) 40%, #374151)" strokeWidth="1" />
                    <circle cx={gridX - 28} cy={gridY + gridH - 8} r="3" fill="#fff" stroke="color-mix(in oklab, var(--ms-primary) 40%, #374151)" strokeWidth="1" />
                    <text x={gridX - 62} y={gridY + 14} fontSize="11" fill="color-mix(in oklab, var(--ms-primary) 35%, #6B7280)">08:00</text>
                    <text x={gridX - 62} y={gridY + gridH - 2} fontSize="11" fill="color-mix(in oklab, var(--ms-primary) 35%, #6B7280)">22:00</text>
                </g>
            </svg>
        </section>
    );
}

/* =========================
 * Formulaire (DROITE) — simple, coloré
 * ========================= */
function LabeledInput({ label, className = "", ...props }: { label: string } & ComponentProps<"input">) {
    return (
        <label className="block">
            <span className="mb-1 block text-xs font-medium text-gray-700">{label}</span>
            <input
                {...props}
                className={[
                    "w-full rounded-2xl border border-gray-200 bg-white px-3 py-2 shadow-sm",
                    "focus:border-[color:var(--ms-primary)] focus:outline-none focus:ring-2 focus:ring-[color:var(--ms-primary)]/20",
                    className,
                ].join(" ")}
            />
        </label>
    );
}

function AuthFormCard() {
    const [tab, setTab] = useState<"login" | "register">("login");
    const [email, setEmail] = useState("");
    const [pwd, setPwd] = useState("");
    const [first, setFirst] = useState("");
    const [last, setLast] = useState("");
    const [showPwd, setShowPwd] = useState(false);
    const [msg, setMsg] = useState<string | null>(null);
    const [err, setErr] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    async function onSubmit(e: FormEvent) {
        e.preventDefault();
        setMsg(null);
        setErr(null);

        // validations simples
        const ok = tab === "login" ? email && pwd : email && pwd && first && last;
        if (!ok) { setErr("Champs requis manquants."); return; }

        setLoading(true);
        try {
            if (tab === "login") {
                const res = await fetch(`${API_URL}/auth/login`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, password: pwd })
                });
                if (!res.ok) {
                    const errBody = await res.json().catch(() => ({}));
                    throw new Error(errBody?.message || "Identifiants invalides");
                }
                const data = await res.json() as { accessToken: string; refreshToken?: string };
                localStorage.setItem("accessToken", data.accessToken);
                if (data.refreshToken) localStorage.setItem("refreshToken", data.refreshToken);
            } else {
                // inscription puis connexion automatique
                const resReg = await fetch(`${API_URL}/auth/register`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ firstname: first, lastname: last, email, password: pwd })
                });
                if (!resReg.ok) {
                    const errBody = await resReg.json().catch(() => ({}));
                    throw new Error(errBody?.message || "Inscription impossible");
                }
                // enchaîne sur un login pour récupérer le token
                const res = await fetch(`${API_URL}/auth/login`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, password: pwd })
                });
                if (!res.ok) {
                    const errBody = await res.json().catch(() => ({}));
                    throw new Error(errBody?.message || "Connexion après inscription impossible");
                }
                const data = await res.json() as { accessToken: string; refreshToken?: string };
                localStorage.setItem("accessToken", data.accessToken);
                if (data.refreshToken) localStorage.setItem("refreshToken", data.refreshToken);
            }

            setMsg(tab === "login" ? "Connexion réussie ✅" : "Inscription réussie ✅");
            navigate("/dashboard"); // adapte si ta route diffère
        } catch (e: any) {
            setErr(e?.message || "Erreur d’authentification");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="w-full max-w-md">
            {/* Marque simple */}
            <div className="mb-6 flex flex-col items-center justify-center gap-2 select-none">
                <img
                    src={logo}
                    alt="Logo My Sheïla"
                    className="h-12 w-auto md:h-16"   // >> plus gros : 48px mobile, 64px dès md
                />
                <span className="text-base md:text-lg font-semibold tracking-wide text-gray-900">Welcome to My Sheïla !</span>
            </div>

            {/* Tabs */}
            <div className="mb-5 inline-flex rounded-full border border-[color:var(--ms-primary-100)] bg-white p-1">
                <button onClick={() => setTab("login")} className={`rounded-full px-3 py-2 text-sm ${tab === "login" ? "bg-[color:var(--ms-primary-50)] text-[color:var(--ms-ink)] shadow" : "text-gray-600"}`}>
                    Se connecter
                </button>
                <button onClick={() => setTab("register")} className={`rounded-full px-3 py-2 text-sm ${tab === "register" ? "bg-[color:var(--ms-primary-50)] text-[color:var(--ms-ink)] shadow" : "text-gray-600"}`}>
                    Créer un compte
                </button>
            </div>

            {/* Form */}
            <form onSubmit={onSubmit} className="space-y-4 rounded-2xl border border-[color:var(--ms-primary-100)] bg-white p-6 shadow-sm">
                {tab === "register" && (
                    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                        <LabeledInput label="Prénom" value={first} onChange={(e) => setFirst(e.target.value)} required />
                        <LabeledInput label="Nom" value={last} onChange={(e) => setLast(e.target.value)} required />
                    </div>
                )}
                <LabeledInput label="Email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required autoComplete="email" />
                <div className="relative">
                    <LabeledInput
                        label="Mot de passe"
                        type={showPwd ? "text" : "password"}
                        value={pwd}
                        onChange={(e) => setPwd(e.target.value)}
                        required
                        autoComplete={tab === "login" ? "current-password" : "new-password"}
                    />
                    <button type="button" onClick={() => setShowPwd((s) => !s)} className="absolute right-2 top-8 text-xs text-gray-600">
                        {showPwd ? "Masquer" : "Afficher"}
                    </button>
                </div>

                {err && <p className="text-sm text-red-600">{err}</p>}
                {msg && <p className="text-sm text-green-700">{msg}</p>}

                <button
                  type="submit"
                  disabled={loading}
                  className="inline-flex w-full items-center justify-center rounded-xl px-4 py-2 text-sm font-medium text-white disabled:opacity-60"
                  style={{
                    background: "linear-gradient(90deg, var(--ms-primary) 0%, var(--ms-secondary) 60%, var(--ms-peach) 100%)",
                    boxShadow: "0 10px 28px color-mix(in oklab, var(--ms-primary) 30%, transparent)",
                  }}
                >
                  {loading ? "Patiente..." : (tab === "login" ? "Connexion" : "Créer mon compte")}
                </button>

                <div className="mt-2 text-center text-xs">
                  <button type="button" onClick={() => setTab(tab === "login" ? "register" : "login")} className="underline text-gray-600">
                    {tab === "login" ? "Créer un compte" : "J’ai déjà un compte"}
                  </button>
                </div>

                <div className="flex items-center justify-between text-xs text-gray-600">
                    <label className="inline-flex items-center gap-2">
                        <input type="checkbox" className="h-4 w-4 rounded border-gray-300" /> Se souvenir de moi
                    </label>
                    <a href="#" className="hover:underline">Mot de passe oublié ?</a>
                </div>
            </form>
        </div>
    );
}


export default function AuthPage() {
    return (
        <div className="flex min-h-dvh flex-col bg-gray-50 text-gray-900">
            <style>{`
        :root{
          /* === Palette My Sheïla (violet + secondaires) === */
          --ms-primary: #6E56F6;         /* VIOLET du logo (modifie ici si besoin) */
          --ms-primary-50: #F3EFFF;      /* lavande claire */
          --ms-primary-100: #E6DFFF;     /* bordures douces */
          --ms-secondary: #2ED3B7;       /* teal/menthe */
          --ms-peach: #FFB49B;           /* pêche pour chaleur */
          --ms-ink: #0b1020;
          --ms-ink-soft: #374151;
        }
      `}</style>

            <main className="mx-auto grid w-full flex-1 max-w-7xl grid-cols-1 gap-10 px-4 py-8 md:grid-cols-[minmax(0,1fr)_420px] md:py-12">
                {/* Présentation pleine colonne gauche (masquée en mobile) */}
                <section className="relative hidden md:block">
                    <AppPresentation />
                </section>

                {/* Formulaire à droite */}
                <section className="flex items-start md:items-center">
                    <AuthFormCard />
                </section>
            </main>

            <footer className="h-14 border-t bg-white/90 backdrop-blur">
                <div className="mx-auto flex h-full max-w-7xl items-center justify-between px-4 text-xs text-gray-600">
                    <span>© {new Date().getFullYear()} My Sheïla</span>
                    <nav className="flex items-center gap-4">
                        <a className="hover:underline" href="#">Confidentialité</a>
                        <a className="hover:underline" href="#">Conditions</a>
                        <a className="hover:underline" href="#">Contact</a>
                    </nav>
                </div>
            </footer>
        </div>
    );
}
