import {Outlet} from "react-router-dom";
import {OnboardingProvider} from "../context/onboarding.context.tsx";

/**
 * Ce composant enveloppe l'OnboardingProvider autour de l'Outlet de React Router.
 * Il permet d'utiliser le Provider comme "layout route" dans App.tsx
 * sans générer l'erreur TypeScript 'children is missing'.
 */
export default function OnboardingWrapper() {
    return (
        <OnboardingProvider>
            {/* L'Outlet rend les routes enfants définies dans App.tsx
                (/onboarding et /dashboard) avec le contexte disponible. */}
            <Outlet/>
        </OnboardingProvider>
    );
}
