import {createContext, type ReactNode, useContext, useEffect, useState} from 'react';
import {getUserStatus} from '../services/onboarding.service';
import {api} from '../lib/api';
import {getAccess} from '../lib/auth';

type OnboardingContextType = {
    isOnboardingComplete: boolean;
    loadingStatus: boolean;
    setIsOnboardingComplete: (isComplete: boolean) => void;
};

const OnboardingContext = createContext<OnboardingContextType | undefined>(undefined);

export function OnboardingProvider({children}: { children: ReactNode }) {
    const [isOnboardingComplete, setIsOnboardingComplete] = useState(false);
    const [loadingStatus, setLoadingStatus] = useState(true);

    // Fonction pour charger le statut initial
    const fetchStatus = async () => {
        if (!getAccess()) {
            // Pas de token, pas d'utilisateur authentifié (ProtectedRoute devrait gérer ça).
            setIsOnboardingComplete(false);
            setLoadingStatus(false);
            return;
        }

        try {
            const status = await getUserStatus();
            setIsOnboardingComplete(status.isOnboardingComplete);
        } catch (error) {
            console.error("Erreur lors de la récupération du statut d'onboarding:", error);
            // En cas d'erreur API, on suppose qu'il faut refaire l'onboarding pour sécurité
            setIsOnboardingComplete(false);
        } finally {
            setLoadingStatus(false);
        }
    };

    useEffect(() => {
        fetchStatus();

        // Intercepteur Axios pour écouter les 403/401 sur l'onboarding
        // Si le backend répond 403/401, on relance le fetch du statut pour s'assurer
        // qu'on redirige l'utilisateur s'il n'a pas terminé l'onboarding.
        const interceptor = api.interceptors.response.use(
            response => response,
            error => {
                if (error.response && error.response.status === 403 && error.config.url?.includes('/onboarding/')) {
                    fetchStatus();
                }
                return Promise.reject(error);
            }
        );

        return () => {
            api.interceptors.response.eject(interceptor);
        };
    }, []);

    return (
        <OnboardingContext.Provider value={{isOnboardingComplete, loadingStatus, setIsOnboardingComplete}}>
            {children}
        </OnboardingContext.Provider>
    );
}

export const useOnboardingContext = () => {
    const context = useContext(OnboardingContext);
    if (context === undefined) {
        throw new Error('useOnboardingContext doit être utilisé à l\'intérieur d\'un OnboardingProvider');
    }
    return context;
};
