CREATE SEQUENCE psn4_seq
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

  ALTER TABLE psn4 ALTER COLUMN id SET DEFAULT nextval('psn4_seq'::regclass);

insert INTO psn4
values (nextval('psn4_seq'), 'Jon Snow', 'M', 'Targaryen', 10, 800, 8);

insert INTO psn4
values (nextval('psn4_seq'), 'Frank Castle', 'M', 'Punisher', 7, 430, 6);

insert INTO psn4
values (nextval('psn4_seq'), 'Ciri', 'F', 'Zirael', 15, 8200, 9);

insert INTO psn4
values (nextval('psn4_seq'), 'Ellie', 'F', 'Wolf', 7, 500, 10);