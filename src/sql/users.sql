DROP TABLE IF EXISTS users;

CREATE TABLE users (

	id BIGSERIAL PRIMARY KEY NOT NULL,
	email VARCHAR(255) UNIQUE NOT NULL,
	password VARCHAR(255) NOT NULL,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL

);