import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";

type Goal = { id: number; name: string; category: "Physique" | "Mental" | "Spiritualité" };
type Slot = { day: string; part: "morning" | "afternoon" | "evening" };

const API = import.meta.env.VITE_API_URL;

export default function OnboardingPage() {
    const [step, setStep] = useState<1 | 2 | 3>(1);
    const [loading, setLoading] = useState(false);
    const [goals, setGoals] = useState<Goal[]>([]);
    const [selected, setSelected] = useState<number[]>([]);
    const [slots, setSlots] = useState<Slot[]>([]);
    const nav = useNavigate();

    // 1) au mount : si déjà onboardé -> dashboard
    useEffect(() => {
        (async () => {
            const r = await fetch(`${API}/api/onboarding/status`, {credentials: "include"});
            if (r.ok) {
                const d = await r.json();
                if (d.onboarded) nav("/dashboard");
            }
        })();
    }, [nav]);

    // 2) charger la liste des objectifs (préconfigurée côté back)
    useEffect(() => {
        (async () => {
            try {
                const r = await fetch(`${API}/api/goals/templates`, {credentials: "include"});
                if (r.ok) setGoals(await r.json());
                else setGoals([
                    // fallback si l’endpoint n’est pas encore prêt
                    {id: 1, name: "Marche 20 min", category: "Physique"},
                    {id: 2, name: "Méditation 10 min", category: "Spiritualité"},
                    {id: 3, name: "Journal de gratitude", category: "Mental"},
                    {id: 4, name: "Étirements", category: "Physique"},
                    {id: 5, name: "Lecture 15 min", category: "Mental"},
                ]);
            } catch { /* noop */
            }
        })();
    }, []);

    // handlers
    const toggleGoal = (id: number) => {
        setSelected(s => s.includes(id) ? s.filter(x => x !== id) : [...s, id]);
    };

    const toggleSlot = (day: string, part: Slot["part"]) => {
        const key = (s: Slot) => `${s.day}-${s.part}`;
        const target: Slot = {day, part};
        setSlots(cur => cur.find(s => key(s) === key(target))
            ? cur.filter(s => !(s.day === day && s.part === part))
            : [...cur, target]);
    };

    const saveGoals = async () => {
        setLoading(true);
        try {
            await fetch(`${API}/api/onboarding/goals`, {
                method: "POST",
                credentials: "include",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({goalIds: selected}),
            });
            setStep(2);
        } finally {
            setLoading(false);
        }
    };

    const saveAvail = async () => {
        setLoading(true);
        try {
            // on transforme morning/afternoon/evening en heures (simple mapping)
            const mapPart = (p: Slot["part"]) =>
                p === "morning" ? ["08:00", "12:00"] : p === "afternoon" ? ["13:30", "17:30"] : ["18:30", "21:00"];

            const payload = {
                slots: slots.map(s => {
                    const [hourBegin, hourEnd] = mapPart(s.part);
                    return {day: s.day, hourBegin, hourEnd};
                })
            };

            await fetch(`${API}/api/onboarding/availabilities`, {
                method: "POST",
                credentials: "include",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload),
            });
            setStep(3);
        } finally {
            setLoading(false);
        }
    };

    const finish = async () => {
        setLoading(true);
        try {
            await fetch(`${API}/api/onboarding/complete`, {
                method: "POST",
                credentials: "include",
            });
            nav("/dashboard");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="page-wrap">
            <main className="card">
                {step === 1 && (
                    <>
                        <h1 className="h-title">Bienvenue 👋</h1>
                        <p className="h-sub">Commence par choisir tes objectifs dans les 3 axes : Physique, Mental,
                            Spiritualité.</p>

                        <div className="grid grid-goals" aria-label="Sélection d’objectifs">
                            {goals.map(g => (
                                <button
                                    key={g.id}
                                    onClick={() => toggleGoal(g.id)}
                                    className={`goal-card ${selected.includes(g.id) ? "active" : ""}`}
                                    type="button"
                                >
                                    <div className="goal-cat">{g.category}</div>
                                    <div className="goal-name">{g.name}</div>
                                </button>
                            ))}
                        </div>
                    </>
                )}

                {step === 2 && (
                    <>
                        <h1 className="h-title">Tes disponibilités</h1>
                        <p className="h-sub">Indique quand tu es généralement disponible. Tu pourras ajuster plus
                            tard.</p>

                        <AvailabilityTable selected={slots} onToggle={toggleSlot}/>
                    </>
                )}

                {step === 3 && (
                    <>
                        <h1 className="h-title">C’est bon ✨</h1>
                        <p className="h-sub">
                            {selected.length} objectif(s) sélectionné(s) • {slots.length} créneau(x) déclaré(s).
                            On prépare un premier planning, ajustable ensuite.
                        </p>

                        <Summary selected={selected} goals={goals} slots={slots}/>
                    </>
                )}
            </main>

            <footer className="actions">
                {step > 1 &&
                    <button className="btn-ghost" onClick={() => setStep((s) => (s === 2 ? 1 : 2))}>Retour</button>}
                {step === 1 && (
                    <button className="btn-primary" disabled={!selected.length || loading} onClick={saveGoals}>
                        {loading ? "Enregistrement..." : "Continuer"}
                    </button>
                )}
                {step === 2 && (
                    <button className="btn-primary" disabled={!slots.length || loading} onClick={saveAvail}>
                        {loading ? "Enregistrement..." : "Continuer"}
                    </button>
                )}
                {step === 3 && (
                    <button className="btn-primary" onClick={finish} disabled={loading}>
                        {loading ? "Génération..." : "Terminer"}
                    </button>
                )}
            </footer>
        </div>
    );
}

/* ---------- Composants secondaires ---------- */

function AvailabilityTable({
                               selected, onToggle
                           }: {
    selected: Slot[];
    onToggle: (day: string, part: Slot["part"]) => void;
}) {
    const days = ["Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"];
    const parts: Slot["part"][] = ["morning", "afternoon", "evening"];
    const partLbl = (p: Slot["part"]) => p === "morning" ? "Matin" : p === "afternoon" ? "Après-midi" : "Soir";

    const isChecked = (d: string, p: Slot["part"]) =>
        selected.some(s => s.day === d && s.part === p);

    return (
        <table className="avail-grid" role="grid">
            <thead>
            <tr>
                <th>Jour</th>
                {parts.map(p => <th key={p}>{partLbl(p)}</th>)}
            </tr>
            </thead>
            <tbody>
            {days.map(day => (
                <tr key={day}>
                    <td style={{textAlign: "left", fontWeight: 600, color: "#2b3556"}}>{day}</td>
                    {parts.map(p => (
                        <td key={p}>
                            <label className="avail-chip">
                                <input
                                    type="checkbox"
                                    checked={isChecked(day, p)}
                                    onChange={() => onToggle(day, p)}
                                />
                                <span>Sélectionner</span>
                            </label>
                        </td>
                    ))}
                </tr>
            ))}
            </tbody>
        </table>
    );
}

function Summary({
                     selected, goals, slots
                 }: {
    selected: number[];
    goals: Goal[];
    slots: Slot[];
}) {
    const byId = new Map(goals.map(g => [g.id, g]));
    const daysOrder = ["Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"];
    const partsOrder: Slot["part"][] = ["morning", "afternoon", "evening"];
    const partLbl = (p: Slot["part"]) => p === "morning" ? "Matin" : p === "afternoon" ? "Après-midi" : "Soir";

    return (
        <div className="grid" style={{marginTop: 8}}>
            <div>
                <h3 className="h-sub" style={{marginBottom: 8, fontWeight: 700, color: "#1f2544"}}>Objectifs</h3>
                <div className="grid grid-goals">
                    {selected.map(id => {
                        const g = byId.get(id);
                        if (!g) return null;
                        return (
                            <div key={id} className="goal-card">
                                <div className="goal-cat">{g.category}</div>
                                <div className="goal-name">{g.name}</div>
                            </div>
                        );
                    })}
                </div>
            </div>
            <div>
                <h3 className="h-sub"
                    style={{margin: "14px 0 8px", fontWeight: 700, color: "#1f2544"}}>Disponibilités</h3>
                <div className="grid" style={{gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))"}}>
                    {daysOrder.map(d => {
                        const daySlots = slots.filter(s => s.day === d);
                        if (!daySlots.length) return null;
                        return (
                            <div key={d} className="goal-card">
                                <div className="goal-name" style={{marginBottom: 8}}>{d}</div>
                                <div className="grid" style={{gridTemplateColumns: "1fr"}}>
                                    {partsOrder.map(p => daySlots.some(s => s.part === p) && (
                                        <span key={p} className="avail-chip">{partLbl(p)}</span>
                                    ))}
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>
        </div>
    );
}