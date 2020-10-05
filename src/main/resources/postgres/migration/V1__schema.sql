CREATE TABLE psn4(
id int PRIMARY KEY,
nome varchar(30) NOT NULL,
genero varchar(1),
idtag varchar(30) NOT NULL,
jogos int,
trofeu int DEFAULT 0,
avaliacao int
);