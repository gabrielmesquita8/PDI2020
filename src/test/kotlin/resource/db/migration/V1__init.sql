CREATE TABLE psn4(
`id` int IDENTITY(1,1) PRIMARY KEY,
`nome` varchar(30) NOT NULL,
`genero` varchar(1),
`idtag` varchar(30) NOT NULL,
`jogos` int,
`trofeu` int DEFAULT 0,
`avaliacao` int
);

