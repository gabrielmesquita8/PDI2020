--insert INTO psn4
--values (1, 'Jon Snow', 'M', 'Targaryen', 10, 800, 8);

--insert INTO psn4
--values (2, 'Frank Castle', 'M', 'Punisher', 7, 430, 6);

--insert INTO psn4
--values (3, 'Ciri', 'F', 'Zirael', 15, 8200, 9);

--insert INTO psn4
--values (4, 'Ellie', 'F', 'Wolf', 7, 500, 10);

CREATE SEQUENCE psn4_seq
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

  ALTER TABLE psn4 ALTER COLUMN id SET DEFAULT nextval('psn4_seq');

insert INTO psn4
values (nextval('psn4_seq'), 'Jon Snow', 'M', 'Targaryen', 10, 800, 8);

insert INTO psn4
values (nextval('psn4_seq'), 'Frank Castle', 'M', 'Punisher', 7, 430, 6);

insert INTO psn4
values (nextval('psn4_seq'), 'Ciri', 'F', 'Zirael', 15, 8200, 9);

insert INTO psn4
values (nextval('psn4_seq'), 'Ellie', 'F', 'Wolf', 7, 500, 10);