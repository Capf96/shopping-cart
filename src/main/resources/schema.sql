create table APP_USER
(
  USER_ID           BIGINT not null,
  USERNAME         VARCHAR(36) not null,
  ENCRYPTED_PASSWORD VARCHAR(128) not null,
  ENABLED           Int not null,
  EMAIL            VARCHAR(128) not null,
  CREATED_AT       date default now(),
  UPDATED_AT       date default now(),
  FIRST_NAME       VARCHAR(128),
  LAST_NAME        VARCHAR(128),
  PHONE_NUMBER     VARCHAR(17),
  MONEY            double precision
);
--
alter table APP_USER
  add constraint APP_USER_PK primary key (USER_ID);

alter table APP_USER
  add constraint APP_USER_UK unique (USERNAME);


-- Create table
create table APP_ROLE
(
  ROLE_ID   BIGINT not null,
  ROLE_NAME VARCHAR(30) not null
);
--
alter table APP_ROLE
  add constraint APP_ROLE_PK primary key (ROLE_ID);

alter table APP_ROLE
  add constraint APP_ROLE_UK unique (ROLE_NAME);


-- Create table
create table USER_ROLE
(
  ID      BIGINT not null,
  USER_ID BIGINT not null,
  ROLE_ID BIGINT not null
);
--
alter table USER_ROLE
  add constraint USER_ROLE_PK primary key (ID);

alter table USER_ROLE
  add constraint USER_ROLE_UK unique (USER_ID, ROLE_ID);

alter table USER_ROLE
  add constraint USER_ROLE_FK1 foreign key (USER_ID)
  references APP_USER (USER_ID) on delete cascade;

alter table USER_ROLE
  add constraint USER_ROLE_FK2 foreign key (ROLE_ID)
  references APP_ROLE (ROLE_ID) on delete cascade;

create table RATINGS
(
  RATING_ID BIGINT not null,
  RATED     BIGINT not null,
  RATER     BIGINT not null,
  RATING    Int    not null
);

alter table RATINGS
  add constraint RATING_PK primary key (RATING_ID);

alter table RATINGS
  add constraint RATING_FK1 foreign key (RATED)
  references APP_USER (USER_ID) on delete cascade;

alter table RATINGS
  add constraint RATING_FK2 foreign key (RATER)
  references APP_USER (USER_ID) on delete cascade;

create table PRODUCTS
(
  PRODUCT_ID    BIGINT,
  NAME          VARCHAR(64) not null,
  DESCRIPTION   TEXT,
  SELLER        BIGINT,
  PRICE         DOUBLE PRECISION,
  VISIBILITY    BOOLEAN,
  QUANTITY      INTEGER
);

alter table PRODUCTS
  add constraint PRODUCTS_PK primary key (PRODUCT_ID);

alter table PRODUCTS
  add constraint PRODUCTS_FK1 foreign key (SELLER)
  references APP_USER (USER_ID) on delete cascade;

create table TRUST
(
  TRUST_ID    BIGINT,
  TRUSTER     BIGINT,
  TRUSTEE     BIGINT
);

alter table TRUST
    add constraint TRUST_PK primary key (TRUST_ID);

alter table TRUST
    add constraint  TRUST_FK1 foreign key (TRUSTER)
    references APP_USER (USER_ID) on delete cascade;

alter table TRUST
    add constraint  TRUST_FK2 foreign key (TRUSTEE)
    references APP_USER (USER_ID) on delete cascade;

alter table TRUST
    add constraint TRUST_UK unique (TRUSTER, TRUSTEE);

-- Used by Spring Remember Me API.
CREATE TABLE Persistent_Logins (
    username varchar(64) not null,
    series varchar(64) not null,
    token varchar(64) not null,
    last_used timestamp not null,
    PRIMARY KEY (series)
);

------------------------

insert into app_role (role_id, role_name) values (1, 'ROLE_ADMIN');

insert into app_role (role_id, role_name) values (2, 'ROLE_SELLER');

insert into app_role (role_id, role_name) values (3, 'ROLE_BUYER');

insert into app_user (user_id, username, encrypted_password, enabled) values (2, 'user', '$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 1);

insert into app_user (user_id, username, encrypted_password, enabled) values (1, 'admin', '$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 1);

insert into user_role (id, user_id, role_id) values (1, 1, 1);

insert into user_role (id, user_id, role_id) values (2, 1, 2);

insert into user_role (id, user_id, role_id) values (3, 2, 2);