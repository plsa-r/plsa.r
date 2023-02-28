create table activities(
    id  bigint PRIMARY KEY AUTO_INCREMENT,
    description character varying(254)
);

create table users(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    name character varying(155) NOT NULL,
    phone character varying(40),
    email character varying(254) NOT NULL,
    passwd character varying(155) NOT NULL,
    time_created bigint default 0
);

create table roles(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    name character varying(155) NOT NULL UNIQUE
);

create table user_permissions(
    user_id bigint REFERENCES users(id),
    permission character varying(55)
);

create table user_roles(
    role_id bigint NOT NULL REFERENCES roles(id),
    user_id bigint NOT NULL REFERENCES users(id)
);


