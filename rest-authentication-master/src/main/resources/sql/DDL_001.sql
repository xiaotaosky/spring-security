-- PostgreSQL脚本

-- 用户表
create table users(
	username character varying(50) not null primary key,
	password character varying(50) not null,
	enabled boolean not null
);

-- 分组表
create table groups (
	id integer not null primary key,
	group_name character varying(50) not null
);

-- 分组权限表
create table group_authorities (
	group_id integer not null,
	authority character varying(50) not null,
	constraint fk_group_authorities_group foreign key(group_id) references groups(id)
);

-- 用户分组表
create table group_members (
	id SERIAL primary key,
	username character varying(50) not null,
	group_id integer not null,
	constraint fk_group_members_group foreign key(group_id) references groups(id)
);

-- token记录表
create table rest_token (
  id SERIAL primary key,
  username character varying(50) not null ,
  token character varying(50),
  create_time timestamp,
  expiried_time timestamp
);

-- 用户定义
insert into users values('admin','123456',true);
insert into users values('manager1','123456',true);
insert into users values('manager2','123456',true);
insert into users values('user1','123456',true);
insert into users values('user2','123456',true);
insert into users values('user3','123456',true);
insert into users values('special','123456',true);
-- 分组定义
insert into groups(id, group_name) values(1, 'GROUP_A');
insert into groups(id, group_name) values(2, 'GROUP_B');
insert into groups(id, group_name) values(3, 'GROUP_C');
insert into groups(id, group_name) values(4, 'GROUP_D');
-- 组权限定义
insert into group_authorities values(1,'ROLE_ADMIN');
insert into group_authorities values(1,'ROLE_MANAGER');
insert into group_authorities values(1,'ROLE_SPECIAL');
insert into group_authorities values(1,'ROLE_USER');
insert into group_authorities values(2,'ROLE_SPECIAL');
insert into group_authorities values(2,'ROLE_USER');
insert into group_authorities values(3,'ROLE_MANAGER');
insert into group_authorities values(3,'ROLE_USER');
insert into group_authorities values(4,'ROLE_USER');
--分组成员
insert into group_members(username,group_id) values('admin',1);
insert into group_members(username,group_id) values('special',2);
insert into group_members(username,group_id) values('manager1',3);
insert into group_members(username,group_id) values('manager2',3);
insert into group_members(username,group_id) values('user1',4);
insert into group_members(username,group_id) values('user2',4);
insert into group_members(username,group_id) values('user3',4);

