-- =========================================================================
-- Insertion des Objectifs pour la catégorie 'Bien-être physique'
-- =========================================================================
INSERT INTO goals (id_category, name, periodicity, frequency)
VALUES ((SELECT id_category FROM categories WHERE name = 'Bien-être physique'),
        'Améliorer mon endurance cardiovasculaire', 'Semaine', 3),
       ((SELECT id_category FROM categories WHERE name = 'Bien-être physique'), 'Atteindre un poids santé idéal',
        'Semaine', 7), -- Fréquence journalière car l'effort est continu (alimentation, hydratation)
       ((SELECT id_category FROM categories WHERE name = 'Bien-être physique'), 'Développer ma souplesse et ma posture',
        'Semaine', 4);

-- =========================================================================
-- Insertion des Objectifs pour la catégorie 'Bien-être mental'
-- =========================================================================
INSERT INTO goals (id_category, name, periodicity, frequency)
VALUES ((SELECT id_category FROM categories WHERE name = 'Bien-être mental'), 'Réduire mon niveau de stress quotidien',
        'Jour', 1),
       ((SELECT id_category FROM categories WHERE name = 'Bien-être mental'),
        'Cultiver une nouvelle compétence intellectuelle', 'Semaine', 5),
       ((SELECT id_category FROM categories WHERE name = 'Bien-être mental'), 'Améliorer la qualité de mon sommeil',
        'Jour', 1);

-- =========================================================================
-- Insertion des Objectifs pour la catégorie 'Bien-être spirituel'
-- =========================================================================
INSERT INTO goals (id_category, name, periodicity, frequency)
VALUES ((SELECT id_category FROM categories WHERE name = 'Spiritualité'),
        'Renforcer ma connexion intérieure et ma pleine conscience', 'Jour', 1),
       ((SELECT id_category FROM categories WHERE name = 'Spiritualité'),
        'Exprimer ma gratitude et ma positivité', 'Jour', 1),
       ((SELECT id_category FROM categories WHERE name = 'Spiritualité'),
        'Me déconnecter du digital pour me recentrer', 'Semaine', 2);