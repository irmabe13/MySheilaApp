import {api} from "../lib/api";
import type {
    AvailabilityRequest,
    GoalResponse,
    UserGoalSelectionRequest,
    WeeklyAvailabilityRequest,
} from "../types/onboarding";

// Structure de réponse pour le statut d'onboarding
export type UserStatusResponse = {
    isOnboardingComplete: boolean;
};

/**
 * Récupère le statut d'onboarding de l'utilisateur.
 * Utilisé principalement par le Dashboard ou le contexte.
 */
export async function getUserStatus(): Promise<UserStatusResponse> {
    const {data} = await api.get<UserStatusResponse>("/onboarding/status");
    return data;
}

/**
 * Récupère la liste de tous les objectifs disponibles avec leur statut de sélection actuel.
 */
export async function getAllGoals(): Promise<GoalResponse[]> {
    const {data} = await api.get<GoalResponse[]>("/onboarding/goals");
    return data;
}

/**
 * Enregistre les objectifs sélectionnés par l'utilisateur.
 * @param payload La liste des IDs des objectifs sélectionnés.
 */
export async function saveSelectedGoals(payload: UserGoalSelectionRequest): Promise<void> {
    await api.post("/onboarding/goals", payload);
}

/**
 * Enregistre les disponibilités hebdomadaires de l'utilisateur.
 * @param payload La liste des créneaux de disponibilité.
 */
export async function saveAvailabilities(payload: WeeklyAvailabilityRequest): Promise<void> {
    await api.post("/onboarding/availability/save", payload);
}

/**
 * Récupère les disponibilités hebdomadaires existantes pour l'édition (optionnel).
 */
export async function getAvailabilities(): Promise<AvailabilityRequest[]> {
    const {data} = await api.get<AvailabilityRequest[]>("/onboarding/availabilities");
    return data;
}
