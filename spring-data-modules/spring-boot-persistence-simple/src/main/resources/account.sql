DROP SCHEMA PUBLIC CASCADE;

create table ACCOUNT
(
   ID      char(8) PRIMARY KEY,
   BALANCE NUMERIC(28, 10)
);

insert into ACCOUNT(ID, BALANCE)
values ('a0000001', 1000);
insert into ACCOUNT(ID, BALANCE)
values ('a0000002', 2000);