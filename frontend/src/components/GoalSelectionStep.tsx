import {useEffect, useState} from 'react';
import type {GoalResponse, UserGoalSelectionRequest} from '../types/onboarding';
import {getAllGoals, saveSelectedGoals} from '../services/onboarding.service';
//import {GoalCard} from './GoalCard.tsx'; // Supposition d'un composant d'affichage de Goal

type GoalSelectionStepProps = {
    onNext: () => void;
};

/**
 * Étape 1 : Sélection des objectifs de l'utilisateur.
 */
export default function GoalSelectionStep({onNext}: GoalSelectionStepProps) {
    const [goals, setGoals] = useState<GoalResponse[]>([]);
    const [selectedGoalIds, setSelectedGoalIds] = useState<number[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        setLoading(true);
        getAllGoals()
            .then(data => {
                setGoals(data);
                // Présélectionner ceux marqués par le backend
                const initialIds = data.filter(g => g.isSelected).map(g => g.idGoal);
                setSelectedGoalIds(initialIds);
            })
            .catch(() => setError("Impossible de charger les objectifs."))
            .finally(() => setLoading(false));
    }, []);

    const toggleGoal = (goalId: number) => {
        setSelectedGoalIds(prev =>
            prev.includes(goalId)
                ? prev.filter(id => id !== goalId)
                : [...prev, goalId]
        );
    };

    const handleSaveAndNext = async () => {
        if (selectedGoalIds.length === 0) {
            setError("Veuillez sélectionner au moins un objectif pour continuer.");
            return;
        }

        setLoading(true);
        setError(null);
        try {
            const payload: UserGoalSelectionRequest = {goalIds: selectedGoalIds};
            await saveSelectedGoals(payload);
            onNext();
        } catch (e) {
            setError("Erreur lors de la sauvegarde des objectifs.");
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div className="text-center py-10">Chargement des objectifs...</div>;
    }

    return (
        <div className="space-y-6">
            <h2 className="text-3xl font-bold text-ms-ink">Étape 1: Choisissez vos objectifs</h2>
            <p className="text-gray-600 max-w-2xl mx-auto">Sélectionnez les domaines de bien-être sur lesquels vous
                souhaitez vous concentrer. Vous pourrez les modifier plus tard.</p>

            {error && <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
                           role="alert">{error}</div>}

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 max-w-5xl mx-auto">
                {goals.map(goal => (
                    <GoalCard
                        key={goal.idGoal}
                        goal={goal}
                        isSelected={selectedGoalIds.includes(goal.idGoal)}
                        onClick={() => toggleGoal(goal.idGoal)}
                    />
                ))}
            </div>

            <div className="flex justify-center pt-8">
                <button
                    onClick={handleSaveAndNext}
                    disabled={loading || selectedGoalIds.length === 0}
                    className="px-8 py-3 bg-ms-primary text-white font-semibold rounded-xl shadow-lg hover:bg-ms-primary-600 transition disabled:opacity-50"
                >
                    {loading ? 'Sauvegarde...' : 'Continuer (Disponibilités)'}
                </button>
            </div>
        </div>
    );
}

// Composant d'affichage des objectifs (doit être créé si non existant).
// J'utilise une simple carte pour l'exemple.
export function GoalCard({goal, isSelected, onClick}: {
    goal: GoalResponse,
    isSelected: boolean,
    onClick: () => void
}) {
    return (
        <div
            onClick={onClick}
            className={`goal-card transition duration-200 cursor-pointer p-4 rounded-xl border-2 ${
                isSelected ? 'border-ms-primary shadow-lg scale-[1.02] bg-ms-primary-50' : 'border-gray-200 hover:shadow-md'
            }`}
        >
            <p className="text-sm font-medium text-gray-500 goal-cat">{goal.categoryName}</p>
            <h3 className="text-lg font-semibold goal-name">{goal.name}</h3>
            <p className="text-xs mt-1 text-gray-400">
                {goal.frequency} fois par {goal.periodicity}
            </p>
        </div>
    );
}
