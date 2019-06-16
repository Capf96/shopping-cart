create table APP_USER
(
  USERNAME           VARCHAR(36) not null,
  ENCRYPTED_PASSWORD VARCHAR(128) not null,
  ENABLED            Int not null,
  EMAIL              VARCHAR(128),
  CREATED_AT         date default now(),
  UPDATED_AT         date default now(),
  FIRST_NAME         VARCHAR(128),
  LAST_NAME          VARCHAR(128),
  PHONE_NUMBER       VARCHAR(17),
  MONEY              double precision
);
--
alter table APP_USER
  add constraint APP_USER_PK primary key (USERNAME);

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
  USERNAME VARCHAR(36) not null,
  ROLE_ID  BIGINT not null
);

alter table USER_ROLE
  add constraint USER_ROLE_UK primary key (USERNAME, ROLE_ID);

alter table USER_ROLE
  add constraint USER_ROLE_FK1 foreign key (USERNAME)
  references APP_USER (USERNAME) on delete cascade;

alter table USER_ROLE
  add constraint USER_ROLE_FK2 foreign key (ROLE_ID)
  references APP_ROLE (ROLE_ID) on delete cascade;

--

create table RATINGS
(
  RATING_ID BIGINT not null,
  RATED     VARCHAR(36) not null,
  RATER     VARCHAR(36) not null,
  RATING    Int    not null
);

alter table RATINGS
  add constraint RATING_PK primary key (RATING_ID);

alter table RATINGS
  add constraint RATING_FK1 foreign key (RATED)
  references APP_USER (USERNAME) on delete cascade;

alter table RATINGS
  add constraint RATING_FK2 foreign key (RATER)
  references APP_USER (USERNAME) on delete cascade;

create table PRODUCTS
(
  PRODUCT_ID    BIGINT,
  NAME          VARCHAR(64) not null,
  DESCRIPTION   TEXT,
  SELLER        VARCHAR(36),
  PRICE         DOUBLE PRECISION,
  VISIBILITY    BOOLEAN,
  QUANTITY      INTEGER
);

alter table PRODUCTS
  add constraint PRODUCTS_PK primary key (PRODUCT_ID);

alter table PRODUCTS
  add constraint PRODUCTS_FK1 foreign key (SELLER)
  references APP_USER (USERNAME) on delete cascade;

create table TRUST
(
  TRUSTER     VARCHAR(36),
  TRUSTEE     VARCHAR(36)
);

alter table TRUST
    add constraint TRUST_PK primary key (TRUSTER, TRUSTEE);

alter table TRUST
    add constraint  TRUST_FK1 foreign key (TRUSTER)
    references APP_USER (USERNAME) on delete cascade;

alter table TRUST
    add constraint  TRUST_FK2 foreign key (TRUSTEE)
    references APP_USER (USERNAME) on delete cascade;

create table CART
(
  USERNAME    VARCHAR(36) not null,
  PRODUCT_ID  BIGINT not null,
  QUANTITY    Integer not null
);

alter table CART
    add constraint CART_PK primary key (USERNAME, PRODUCT_ID);

alter table CART
    add constraint CART_FK1 foreign key (USERNAME)
    references APP_USER (USERNAME) on delete cascade;

alter table CART
    add constraint CART_FK2 foreign key (PRODUCT_ID)
    references PRODUCTS (PRODUCT_ID) on delete cascade;

create table PURCHASES
(
  PURCHASE_ID       BIGINT not null,
  USERNAME          VARCHAR(36) not null,
  PRODUCT_ID        BIGINT not null,
  DATE_OF_PURCHASE  date default now(),
  QUANTITY          INTEGER not null,
  PRICE             DOUBLE PRECISION not null
);

alter table PURCHASES
    add constraint PURCHASES_PK primary key (PURCHASE_ID);

alter table PURCHASES
    add constraint PURCHASES_FK1 foreign key (USERNAME)
    references APP_USER (USERNAME) on delete cascade;

alter table PURCHASES
    add constraint PURCHASES_FK2 foreign key (PRODUCT_ID)
    references PRODUCTS (PRODUCT_ID) on delete cascade;

create table PRODUCT_IMAGES
(
  PRODUCT_IMAGE_ID  BIGINT not null,
  PRODUCT_ID        BIGINT not null,
  PATH              VARCHAR(256)
);

alter table PRODUCT_IMAGES
    add constraint PRODUCT_IMAGES_PK primary key (PRODUCT_IMAGE_ID);

alter table PRODUCT_IMAGES
    add constraint PRODUCT_IMAGES_FK1 foreign key (PRODUCT_ID)
    references PRODUCTS (PRODUCT_ID);


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

insert into app_user (username, encrypted_password, enabled) values ('user', '$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 1);

insert into app_user (username, encrypted_password, enabled) values ('admin', '$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu', 1);

insert into user_role (username, role_id) values ('admin', 1);

insert into user_role (username, role_id) values ('admin', 2);

insert into user_role (username, role_id) values ('user', 3);