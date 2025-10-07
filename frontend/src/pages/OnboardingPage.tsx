import {useState} from 'react';
import {Navigate} from 'react-router-dom';
import GoalSelectionStep from '../components/GoalSelectionStep';
import AvailabilityStep from '../components/AvailabilityStep';
import {useOnboardingContext} from '../context/onboarding.context';

/**
 * Page principale de l'Onboarding.
 * Gère l'état de la progression de l'utilisateur (Étape 1 ou Étape 2).
 */
export default function OnboardingPage() {
    // 0 : Chargement, 1 : Objectifs, 2 : Disponibilités
    const [step, setStep] = useState(1);
    const {isOnboardingComplete, loadingStatus} = useOnboardingContext();

    if (loadingStatus) {
        return <div className="text-center py-20 text-lg">Vérification du statut...</div>;
    }

    // Si l'onboarding est déjà terminé, rediriger vers le dashboard
    if (isOnboardingComplete) {
        return <Navigate to="/dashboard" replace/>;
    }

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-start p-4 pt-10">
            <h1 className="text-4xl font-extrabold text-ms-primary mb-12">Bienvenue sur My Sheïla</h1>

            <div className="w-full max-w-6xl bg-white p-6 md:p-10 rounded-3xl shadow-2xl">

                {/* Indicateur de progression */}
                <div className="mb-10 flex justify-center space-x-6">
                    <div className={`text-center ${step >= 1 ? 'text-ms-primary font-bold' : 'text-gray-400'}`}>
                        <span
                            className={`inline-flex items-center justify-center w-8 h-8 rounded-full ${step >= 1 ? 'bg-ms-primary text-white' : 'border border-gray-300 text-gray-400'}`}>1</span>
                        <p className="mt-1 text-sm">Objectifs</p>
                    </div>
                    <div className={`text-center ${step === 2 ? 'text-ms-primary font-bold' : 'text-gray-400'}`}>
                        <span
                            className={`inline-flex items-center justify-center w-8 h-8 rounded-full ${step === 2 ? 'bg-ms-primary text-white' : 'border border-gray-300 text-gray-400'}`}>2</span>
                        <p className="mt-1 text-sm">Disponibilités</p>
                    </div>
                </div>

                {/* Contenu de l'étape */}
                <div className="mt-8">
                    {step === 1 && <GoalSelectionStep onNext={() => setStep(2)}/>}
                    {step === 2 && <AvailabilityStep onComplete={() => { /* La navigation se fait dans AvailabilityStep */
                    }}/>}
                </div>
            </div>
        </div>
    );
}
