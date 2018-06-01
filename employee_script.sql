use moviedb;

create table employees (
	email varchar(50),
	password varchar(20) not null,
    fullname varchar(100), 
	primary key (email)
);

INSERT INTO employees VALUES('classta@email.edu', 'classta', 'TA CS122B');genres_in_movies