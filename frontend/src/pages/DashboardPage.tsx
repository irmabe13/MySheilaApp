import {Navigate} from 'react-router-dom';
import {useOnboardingContext} from '../context/onboarding.context';

export default function Dashboard() {
    const {isOnboardingComplete, loadingStatus} = useOnboardingContext();

    if (loadingStatus) {
        return <div className="text-center py-20 text-lg">Chargement du statut...</div>;
    }

    // Si l'onboarding n'est pas complet, rediriger vers la page d'onboarding
    if (!isOnboardingComplete) {
        return <Navigate to="/onboarding" replace/>;
    }

    // Si l'onboarding est complet, afficher le contenu du Dashboard
    return (
        <div className="p-8">
            <h1 className="text-4xl font-bold text-ms-ink mb-6">Tableau de bord</h1>
            <p className="text-xl text-ms-secondary">Votre planning est prêt !</p>
            {/* Ici viendra le contenu réel du dashboard (calendrier, tâches). */}

            <div className="mt-10 p-6 bg-ms-primary-50 rounded-xl max-w-lg mx-auto">
                <p className="text-ms-ink-soft">
                    Félicitations, vous avez terminé l'onboarding ! Maintenant, vous pouvez voir le résultat de votre
                    planning.
                </p>
                {/* Exemple de déconnexion pour le test */}
                <button
                    onClick={() => {
                        localStorage.clear();
                        window.location.href = '/login';
                    }}
                    className="mt-4 px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition"
                >
                    Déconnexion (Test)
                </button>
            </div>
        </div>
    );
}
