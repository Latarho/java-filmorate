merge into MPA_RATING key (ID)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13') ,
           (4, 'R'),
           (5, 'NC-17');

merge into GENRES key (ID)
    VALUES  (1, 'Комедия'),
            (2, 'Драма'),
            (3, 'Мультфильм'),
            (4, 'Ужасы'),
            (5, 'Триллер'),
            (6, 'Детектив');