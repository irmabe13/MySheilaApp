INSERT INTO goals (category_id, name, frequency_type, frequency_count, default_duration_minutes)
VALUES
    ((SELECT id FROM categories WHERE name='Bien-être physique'),
     'Avoir une activité sportive régulière', 'WEEKLY', 3, 30),
    ((SELECT id FROM categories WHERE name='Bien-être physique'),
     'Améliorer mon alimentation', 'DAILY', 1, 15),
    ((SELECT id FROM categories WHERE name='Bien-être physique'),
     'Mieux dormir', 'DAILY', 1, 10);



INSERT INTO goals (category_id, name, frequency_type, frequency_count, default_duration_minutes)
VALUES
    ((SELECT id FROM categories WHERE name='Bien-être mental'),
     'Réduire mon stress', 'DAILY', 1, 15),
    ((SELECT id FROM categories WHERE name='Bien-être mental'),
     'Développer ma créativité', 'WEEKLY', 3, 30),
    ((SELECT id FROM categories WHERE name='Bien-être mental'),
     'Apprendre continuellement', 'WEEKLY', 5, 20);