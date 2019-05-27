create table app_user
(
  user_id BIGINT not null,
  username VARCHAR(50) not null,
  encrypted_password VARCHAR(128) not null,
  enabled INT not null,
  primary key (user_id),
  unique (username)
);

create table app_role
(
  role_id BIGINT not null,
  role_name VARCHAR(30) not null,
  primary key (role_id),
  unique (role_name)
);

create table user_role
(
  id BIGINT not null,
  user_id BIGINT not null,
  role_id BIGINT not null,
  primary key (id),
  unique (user_id),
  unique (role_id),
  foreign key (user_id) references app_user(user_id),
  foreign key (role_id) references app_role(role_id)
);

create table Persisten_Logins
(
  username VARCHAR(64) not null,
  series VARCHAR(64) not null,
  token VARCHAR(64) not null,
  last_used TIMESTAMP not null,
  primary key (username)
);

insert into app_role (role_id, role_name) values (1, 'ROLE_ADMIN');

insert into app_role (role_id, role_name) values (2, 'ROLE_SELLER');

insert into app_role (role_id, role_name) values (3, 'ROLE_BUYER');