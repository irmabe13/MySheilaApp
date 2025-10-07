// Structure de réponse pour un objectif (Goal)
export type GoalResponse = {
    idGoal: number;
    name: string;
    periodicity: string;
    frequency: number;
    idCategory: number;
    categoryName: string;
    isSelected: boolean;
};

// Structure de la requête pour la sélection d'objectifs (Étape 1)
export type UserGoalSelectionRequest = {
    goalIds: number[];
};

// Structure d'un créneau de disponibilité (Étape 2)
export type AvailabilityRequest = {
    dayOfWeek: 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';
    startTime: string; // Format "HH:mm" (e.g., "09:00:00")
    endTime: string; // Format "HH:mm" (e.g., "17:00:00")
};

// Structure de la requête pour les disponibilités (Étape 2)
export type WeeklyAvailabilityRequest = {
    availabilities: AvailabilityRequest[];
};
