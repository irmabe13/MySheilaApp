import {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import type {AvailabilityRequest, WeeklyAvailabilityRequest} from '../types/onboarding';
import {getAvailabilities, saveAvailabilities} from '../services/onboarding.service';
import {useOnboardingContext} from '../context/onboarding.context';

// Définition des jours et des formats d'heure
const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'] as const;
type Day = typeof DAYS[number];

type AvailabilityStepProps = {
    onComplete: () => void;
};

/**
 * Convertit une liste d'AvailabilityRequest en map {[day] : AvailabilityRequest[]}
 */
const groupAvailabilitiesByDay = (availabilities: AvailabilityRequest[]) => {
    return availabilities.reduce((acc, avail) => {
        const day = avail.dayOfWeek;
        if (!acc[day]) {
            acc[day] = [];
        }
        acc[day].push(avail);
        return acc;
    }, {} as Record<Day, AvailabilityRequest[]>);
};

/**
 * Convertit l'heure au format "HH:MM:SS" à "HH:MM" pour l'affichage dans l'input time
 */
const formatTimeForInput = (time: string) => time.substring(0, 5);


/**
 * Étape 2 : Définition des disponibilités hebdomadaires.
 */
export default function AvailabilityStep({onComplete}: AvailabilityStepProps) {
    const navigate = useNavigate();
    const {setIsOnboardingComplete} = useOnboardingContext();

    // Map pour stocker les créneaux par jour
    const [weeklyAvailabilities, setWeeklyAvailabilities] = useState<Record<Day, AvailabilityRequest[]>>(
        DAYS.reduce((acc, day) => ({...acc, [day]: []}), {} as Record<Day, AvailabilityRequest[]>)
    );

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // Chargement des disponibilités existantes pour pré-remplissage
    useEffect(() => {
        getAvailabilities()
            .then(data => {
                const grouped = groupAvailabilitiesByDay(data);
                setWeeklyAvailabilities(prev => ({...prev, ...grouped}));
            })
            .catch(e => console.error("Erreur lors du chargement des disponibilités existantes", e))
            .finally(() => setLoading(false));
    }, []);

    const addSlot = (day: Day) => {
        setWeeklyAvailabilities(prev => ({
            ...prev,
            [day]: [
                ...prev[day],
                {dayOfWeek: day, startTime: '09:00', endTime: '10:00'} // Valeurs par défaut
            ]
        }));
    };

    const removeSlot = (day: Day, index: number) => {
        setWeeklyAvailabilities(prev => ({
            ...prev,
            [day]: prev[day].filter((_, i) => i !== index)
        }));
    };

    const updateSlot = (day: Day, index: number, field: 'startTime' | 'endTime', value: string) => {
        setWeeklyAvailabilities(prev => ({
            ...prev,
            [day]: prev[day].map((slot, i) =>
                i === index
                    ? {...slot, [field]: value} // Assurer format HH:MM
                    : slot
            )
        }));
    };

    const handleSaveAndComplete = async () => {
        // Validation simple : s'assurer qu'au moins un créneau est défini
        const allSlots = Object.values(weeklyAvailabilities).flat();
        if (allSlots.length === 0) {
            setError("Veuillez définir au moins un créneau de disponibilité.");
            return;
        }

        // Validation des heures (début < fin)
        const invalidSlots = allSlots.some(slot => slot.startTime >= slot.endTime);
        if (invalidSlots) {
            setError("Veuillez vérifier vos créneaux : l'heure de début doit être strictement antérieure à l'heure de fin.");
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const payload: WeeklyAvailabilityRequest = {
                availabilities: allSlots.map(s => ({
                    ...s,
                    startTime: s.startTime.slice(0, 5),
                    endTime: s.endTime.slice(0, 5),
                })),
            };
            await saveAvailabilities(payload);

            // Mettre à jour le contexte et naviguer
            setIsOnboardingComplete(true);
            onComplete();
            navigate('/dashboard', {replace: true});

        } catch (e) {
            setError("Erreur lors de la sauvegarde des disponibilités.");
        } finally {
            setLoading(false);
        }
    };


    if (loading) {
        return <div className="text-center py-10">Chargement des disponibilités...</div>;
    }

    return (
        <div className="space-y-6">
            <h2 className="text-3xl font-bold text-ms-ink">Étape 2: Définissez vos disponibilités</h2>
            <p className="text-gray-600 max-w-3xl mx-auto">
                Indiquez les créneaux horaires récurrents où vous êtes libre pour vos objectifs. Plus vous êtes précis,
                plus le planning sera efficace.
            </p>

            {error && <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
                           role="alert">{error}</div>}

            <div className="max-w-4xl mx-auto p-4 md:p-6 bg-white rounded-2xl shadow-xl">
                {DAYS.map(day => (
                    <div key={day} className="py-4 border-b last:border-b-0">
                        <h3 className="text-lg font-semibold text-ms-ink mb-2 capitalize">
                            {day.substring(0, 3)}
                        </h3>

                        <div className="flex flex-col gap-3">
                            {weeklyAvailabilities[day].map((slot, index) => (
                                <div key={index}
                                     className="flex items-center space-x-3 bg-gray-50 p-3 rounded-xl shadow-sm">
                                    <input
                                        type="time"
                                        value={formatTimeForInput(slot.startTime)}
                                        onChange={(e) => updateSlot(day, index, 'startTime', e.target.value)}
                                        className="w-full max-w-[120px] p-2 border border-gray-300 rounded-lg focus:ring-ms-primary focus:border-ms-primary"
                                    />
                                    <span className="text-gray-500">-</span>
                                    <input
                                        type="time"
                                        value={formatTimeForInput(slot.endTime)}
                                        onChange={(e) => updateSlot(day, index, 'endTime', e.target.value)}
                                        className="w-full max-w-[120px] p-2 border border-gray-300 rounded-lg focus:ring-ms-primary focus:border-ms-primary"
                                    />
                                    <button
                                        onClick={() => removeSlot(day, index)}
                                        className="text-red-500 hover:text-red-700 p-2 rounded-full transition"
                                        title="Supprimer le créneau"
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20"
                                             viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"
                                             strokeLinecap="round" strokeLinejoin="round"
                                             className="lucide lucide-trash">
                                            <path d="M3 6h18"/>
                                            <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/>
                                            <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
                                        </svg>
                                    </button>
                                </div>
                            ))}
                            <button
                                onClick={() => addSlot(day)}
                                className="mt-2 w-full max-w-[250px] mx-auto flex items-center justify-center space-x-1 text-ms-primary hover:text-ms-primary-600 transition font-medium text-sm border border-ms-primary/50 hover:border-ms-primary rounded-xl px-4 py-2"
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"
                                     fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round"
                                     strokeLinejoin="round" className="lucide lucide-plus">
                                    <path d="M5 12h14"/>
                                    <path d="M12 5v14"/>
                                </svg>
                                <span>Ajouter un créneau</span>
                            </button>
                        </div>
                    </div>
                ))}
            </div>

            <div className="flex justify-center pt-8">
                <button
                    onClick={handleSaveAndComplete}
                    disabled={loading}
                    className="px-8 py-3 bg-ms-secondary text-white font-semibold rounded-xl shadow-lg hover:bg-ms-secondary/80 transition disabled:opacity-50"
                >
                    {loading ? 'Finalisation...' : 'Terminer & Accéder au Dashboard'}
                </button>
            </div>
        </div>
    );
}
