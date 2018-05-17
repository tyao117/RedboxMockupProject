-- Change DELIMITER to $$
-- want to drop the procedure if it exists
DROP PROCEDURE IF EXISTS moviedb.add_movie;
use moviedb;
DELIMITER $$
CREATE PROCEDURE add_movie(IN in_id VARCHAR(10), IN in_title VARCHAR(100), IN in_year INT, IN in_director VARCHAR(100), IN in_genre VARCHAR (32), IN in_star VARCHAR(32))
BEGIN
	DECLARE genre_id VARCHAR(11);
	DECLARE star_id VARCHAR(10);
	INSERT INTO movies VALUES(in_id, in_title, in_year, in_director);
    SELECT id from genres WHERE in_genre=name INTO genre_id;
    SELECT id from stars WHERE in_star=name INTO star_id;
    INSERT INTO stars_in_movies VALUES (star_id, in_id);
    INSERT INTO genres_in_movies VALUES (genre_id, in_id);
END
$$

DELIMITER ;
--  Its arguments include all the required fields of the movie, a single star (star name) and a single genre (genre name)-- 