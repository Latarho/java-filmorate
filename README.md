# java-filmorate

__Database diagram__

https://app.quickdatabasediagrams.com/#/d/w36iFn

![Filmorate_DBD](https://user-images.githubusercontent.com/53216025/177046855-0c0958f9-1af7-4de0-b25a-e89c93ce6714.png)

__Get user by ID__

`SELECT * 
FROM Users
WHERE id = n;`

__Get film by ID__

`SELECT * 
FROM Films
WHERE id = n;`

__Get films by mpaa_rating__

`SELECT * 
FROM Films
WHERE mpaa_rating = ‘G’;`

__Get films by genre__

`SELECT * 
FROM Films
WHERE id IN (
SELECT id FROM Genres
WHERE name = ‘Комедия’);`
