use moviedb;
ALTER TABLE movies ADD FULLTEXT (title);
set GLOBAL innodb_optimize_fulltext_only=ON;
optimize table movies;