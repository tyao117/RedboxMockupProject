-- Change DELIMITER to $$
DELIMITER $$
CREATE PROCEDURE add_movie(in id VARCHAR(10), in title VARCHAR(100), in year INT, in director VARCHAR(100))
BEGIN

END$$

DELIMITER;
--  Its arguments include all the required fields of the movie, a single star (star name) and a single genre (genre name)-- 