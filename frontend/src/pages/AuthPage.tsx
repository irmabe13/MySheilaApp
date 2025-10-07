import {type FormEvent, useEffect, useState} from "react";
import logo from "../assets/logo.png";
import {useMatch, useNavigate} from "react-router-dom";
import {login, register as userAuth} from "../services/auth.service";
import LabeledInput from "../components/LabeledInput";
import AppPresentation from "../components/AppPresentation";
import axios from "axios";


const MIN_PASSWORD_LENGTH = 12;

function AuthFormCard() {
    const navigate = useNavigate();
    const inRegister = useMatch("/register") !== null;
    const [tab, setTab] = useState<"login" | "register">(inRegister ? "register" : "login");
    const [email, setEmail] = useState("");
    const [pwd, setPwd] = useState("");
    const [first, setFirst] = useState("");
    const [last, setLast] = useState("");
    const [showPwd, setShowPwd] = useState(false);
    const [msg, setMsg] = useState<string | null>(null);
    const [err, setErr] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setTab(inRegister ? "register" : "login");
    }, [inRegister]);

    async function onSubmit(e: FormEvent) {
        e.preventDefault();
        setMsg(null);
        setErr(null);

        // validations simples
        const ok = tab === "login" ? email && pwd : email && pwd && first && last;
        if (!ok) {
            setErr("Champs requis manquants.");
            return;
        }

        if (tab === "register" && pwd.length < MIN_PASSWORD_LENGTH) {
            setErr(`Le mot de passe doit contenir au moins ${MIN_PASSWORD_LENGTH} caractères.`);
            return;
        }


        setLoading(true);
        try {
            if (tab === "login") {
                await login({email, password: pwd});
            } else {
                await userAuth({firstname: first, lastname: last, email, password: pwd});
                await login({email, password: pwd}); // connexion auto
            }
            setMsg(tab === "login" ? "Connexion réussie ✅" : "Inscription réussie ✅");
            navigate("/onboarding", {replace: true});
        } catch (e: any) {
            console.error(e);
            // Amélioration de la gestion des erreurs 400 du back-end (validation)
            let errorMsg = "Erreur lors de l'authentification. Veuillez vérifier vos identifiants ou réessayer.";

            if (axios.isAxiosError(e) && e.response) {
                // Si c'est une erreur 400, on essaie d'extraire le message d'erreur du back-end.
                if (e.response.status === 400) {
                    // Spring peut retourner un objet d'erreur avec un message ou un tableau d'erreurs.
                    const data = e.response.data as any;
                    if (data.message && typeof data.message === 'string') {
                        errorMsg = data.message;
                    } else if (data.errors && Array.isArray(data.errors) && data.errors.length > 0) {
                        // Par exemple, si le back-end renvoie une liste de Field Errors.
                        errorMsg = `Erreurs de validation: ${data.errors.map((err: any) => err.defaultMessage || err.field).join(', ')}`;
                    } else {
                        // Cas par défaut pour 400 Bad Request
                        errorMsg = "Erreur de validation des données (400 Bad Request). Vérifiez le format (Email, Mot de passe min. 12 car.).";
                    }
                } else if (e.response.status === 409) {
                    errorMsg = "Un compte avec cette adresse e-mail existe déjà.";
                } else {
                    errorMsg = `Erreur (${e.response.status}): Une erreur inattendue est survenue.`;
                }
            }
            setErr(errorMsg);
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
                    className="h-12 w-auto md:h-16"
                />
                <span
                    className="text-base md:text-lg font-semibold tracking-wide text-gray-900">Welcome to My Sheïla !</span>
            </div>

            {/* Tabs */}
            <div className="mb-5 inline-flex rounded-full border border-[color:var(--ms-primary-100)] bg-white p-1">
                <button onClick={() => navigate("/login")}
                        className={`rounded-full px-3 py-2 text-sm ${tab === "login" ? "bg-[color:var(--ms-primary-50)] text-[color:var(--ms-ink)] shadow" : "text-gray-600"}`}>
                    Se connecter
                </button>
                <button onClick={() => navigate("/register")}
                        className={`rounded-full px-3 py-2 text-sm ${tab === "register" ? "bg-[color:var(--ms-primary-50)] text-[color:var(--ms-ink)] shadow" : "text-gray-600"}`}>
                    Créer un compte
                </button>
            </div>

            {/* Form */}
            <form onSubmit={onSubmit}
                  className="space-y-4 rounded-2xl border border-[color:var(--ms-primary-100)] bg-white p-6 shadow-sm">
                {tab === "register" && (
                    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                        <LabeledInput label="Prénom" value={first} onChange={(e) => setFirst(e.target.value)} required/>
                        <LabeledInput label="Nom" value={last} onChange={(e) => setLast(e.target.value)} required/>
                    </div>
                )}
                <LabeledInput label="Email" type="email" value={email} onChange={(e) => setEmail(e.target.value)}
                              required autoComplete="email"/>
                <div className="relative">
                    <LabeledInput
                        label="Mot de passe"
                        type={showPwd ? "text" : "password"}
                        value={pwd}
                        onChange={(e) => setPwd(e.target.value)}
                        required
                        autoComplete={tab === "login" ? "current-password" : "new-password"}
                    />
                    <button type="button" onClick={() => setShowPwd((s) => !s)}
                            className="absolute right-2 top-8 text-xs text-gray-600">
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
                    <button type="button" onClick={() => navigate(tab === "login" ? "/register" : "/login")}
                            className="underline text-gray-600">
                        {tab === "login" ? "Créer un compte" : "J’ai déjà un compte"}
                    </button>
                </div>

                <div className="flex items-center justify-between text-xs text-gray-600">
                    <label className="inline-flex items-center gap-2">
                        <input type="checkbox" className="h-4 w-4 rounded border-gray-300"/> Se souvenir de moi
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
          --ms-primary: #6E56F6;         /* VIOLET du logo */
          --ms-primary-50: #F3EFFF;      /* lavande claire */
          --ms-primary-100: #E6DFFF;     /* bordures douces */
          --ms-secondary: #2ED3B7;       /* teal/menthe */
          --ms-peach: #FFB49B;           /* pêche pour chaleur */
          --ms-ink: #0b1020;
          --ms-ink-soft: #374151;
        }
      `}</style>

            <main
                className="mx-auto grid w-full flex-1 max-w-7xl grid-cols-1 gap-10 px-4 py-8 md:grid-cols-[minmax(0,1fr)_420px] md:py-12">
                {/* Présentation pleine colonne gauche (masquée en mobile) */}
                <section className="relative hidden md:block">
                    <AppPresentation/>
                </section>

                {/* Formulaire à droite */}
                <section className="flex items-start md:items-center">
                    <AuthFormCard/>
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
