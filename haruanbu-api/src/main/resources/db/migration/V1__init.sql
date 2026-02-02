-- =========
-- Haruanbu schema init (PostgreSQL)

/*users
- id
- user_name : 닉네임
- password : 비밀번호
- phone : 핸드폰
- email

permission : 권한
- id
- name : 권한명

user_permission : 유저별 권한 리스트
- id
- guardian_id : user테이블 FK
- senior_id : user테이블 FK
- permission_id : permission 테이블 FK
- date_created : 생성일

care_group : 그룹
- id
- user_id : 그룹을 생성한 user의 FK
- group_name : 그룹명
- date_created : 그룹 생성일

group_members : 그룹 가입 멤버
- id
- user_id : 유저 테이블 FK
- group_id : 그룹 테이블 FK
- date_created : 그룹 가입일

invite_link : 초대링크 이력관리 테이블
- id
- host_user_id : 초대한 유저 테이블 FK
- invited_user_id : 초대받은 유저 테이블 FK
- code : 초대토큰
- expires_date : 링크 만료시각
- is_used_link : 초대링크 사용 가입여부
- used_at : 초대링크 수락 시간

routine : 루틴 관리 테이블
- id
- routine_name : 루틴명
- guardian_id : 가디언 user id FK
- senior_id : 시니어 user id FK
- date_created
- schedule : 루틴 스케쥴
- routine_type : routine / event select

routine_check_history  : 루틴 체크 기록
- id
- routine_id : routine 테이블 FK
- senior_id : 시니어 user id FK
- checked_at : 루틴 체크 날짜
- is_checked : 루틴 체크여부


allert_history
- id
- routine_id : 루틴 테이블 FK
- senior_id : 유저 테이블 FK
- date_created
- is_checked : 알람 체크여부

location : 시니어 위치 설정
- id
- location_name : 집/회사/ 등 지정된 위치명
- senior_id : 유저 테이블 FK
- guardian_id : 유저 테이블 FK
- date_created

location_log : 위치 로그
- id
- location : 현재 위치 좌표
- date_created
- senior_id : 유저 테이블 FK

active_log : 핸드폰 사용 로그
- id
- senior_id : 유저 테이블 FK
- date_created
- active_type : 핸드폰 사용 타입

temperature_log : 온도 추적 로그
- id
- senior_id : 유저 테이블 FK
- date_created
- temperature : 현재 온도 기록
- location_id : location 테이블 FK*/

-- =========

create extension if not exists pgcrypto;

-- 1) users
create table users (
   id uuid primary key default gen_random_uuid(),
   user_name varchar(50) not null,          -- 닉네임
   password varchar(255) not null,          -- 해시 저장 권장
   phone varchar(30),
   email varchar(255),
   created_at timestamptz not null default now()
);

create unique index ux_users_phone on users(phone) where phone is not null;
create unique index ux_users_email on users(email) where email is not null;


-- 2) permission
create table permission (
    id uuid primary key default gen_random_uuid(),
    name varchar(100) not null              -- 권한명: ROUTINE_STATUS, LOCATION, INACTIVITY_DETAIL, EVENT_DETAIL 등
);

create unique index ux_permission_name on permission(name);


-- 3) user_permission (guardian <-> senior 관계 기반 동의/권한)
create table user_permission (
     id uuid primary key default gen_random_uuid(),
     guardian_id uuid not null,
     senior_id uuid not null,
     permission_id uuid not null,
     date_created timestamptz not null default now(),
     expiration_time timestamptz not null, -- 예) 위치동의 1시간 공유  / 오늘만 공유 / 항상 공유
     constraint fk_user_permission_guardian foreign key (guardian_id) references users(id) on delete cascade,
     constraint fk_user_permission_senior foreign key (senior_id) references users(id) on delete cascade,
     constraint fk_user_permission_permission foreign key (permission_id) references permission(id) on delete cascade
);

-- 동일 guardian-senior 쌍에 동일 permission 중복 방지
create unique index ux_user_permission_pair on user_permission(guardian_id, senior_id, permission_id);

create index ix_user_permission_senior on user_permission(senior_id);
create index ix_user_permission_guardian on user_permission(guardian_id);


-- 4) care_group
create table care_group (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null,                  -- 생성자
    group_name varchar(100) not null,
    date_created timestamptz not null default now(),
    constraint fk_care_group_creator foreign key (user_id) references users(id) on delete restrict
);

create index ix_care_group_user_id on care_group(user_id);


-- 5) group_members
create table group_members (
   id uuid primary key default gen_random_uuid(),
   user_id uuid not null,
   group_id uuid not null,
   date_created timestamptz not null default now(),
   user_type varchar(30) not null, -- 그룹 내에 가디언인지, 시니어인지
   constraint fk_group_members_user foreign key (user_id) references users(id) on delete cascade,
   constraint fk_group_members_group foreign key (group_id) references care_group(id) on delete cascade
);

-- 같은 그룹에 같은 유저 중복 가입 방지
create unique index ux_group_members_unique on group_members(group_id, user_id);
create index ix_group_members_user on group_members(user_id);


-- 6) invite_link
create table invite_link (
     id uuid primary key default gen_random_uuid(),
     guardian_user_id uuid not null,
     senior_user_id uuid,                   -- 초대 당시 특정 유저가 없으면 null 허용
     code varchar(64) not null,              -- 초대 토큰(짧게)
     expires_date timestamptz not null,
     is_used_link boolean not null default false,
     status varchar(50) not null, -- 초대 수락 현황 (ACCEPTED, REJECTED, EXPIRED)
     used_at timestamptz,
     date_created timestamptz not null default now(),
     constraint fk_invite_link_host foreign key (guardian_user_id) references users(id) on delete cascade,
     constraint fk_invite_link_invited foreign key (senior_user_id) references users(id) on delete set null
);

create unique index ux_invite_link_code on invite_link(code);
create index ix_invite_link_host on invite_link(guardian_user_id);
create index ix_invite_link_expires on invite_link(expires_date);


-- 7) routine
create table routine (
     id uuid primary key default gen_random_uuid(),
     routine_name varchar(100) not null,
     guardian_id uuid not null,
     senior_id uuid not null,
     date_created timestamptz not null default now(),
     schedule text,                          -- MVP는 text OK (실사형이면 schedule 테이블 분리 추천)
     routine_type varchar(20) not null,      -- ROUTINE / EVENT
     constraint fk_routine_guardian foreign key (guardian_id) references users(id) on delete restrict,
     constraint fk_routine_senior foreign key (senior_id) references users(id) on delete restrict
);

create index ix_routine_senior on routine(senior_id);
create index ix_routine_guardian on routine(guardian_id);


-- 8) routine_check_history
create table routine_check_history (
       id uuid primary key default gen_random_uuid(),
       routine_id uuid not null,
       senior_id uuid not null,
       checked_at timestamptz not null,
       is_checked boolean not null default true,
       date_created timestamptz not null default now(),
       constraint fk_rch_routine foreign key (routine_id) references routine(id) on delete cascade,
       constraint fk_rch_senior foreign key (senior_id) references users(id) on delete cascade
);

create index ix_rch_routine_checked_at on routine_check_history(routine_id, checked_at desc);
create index ix_rch_senior_checked_at on routine_check_history(senior_id, checked_at desc);


-- 9) alert_history
create table alert_history (
    id uuid primary key default gen_random_uuid(),
    routine_id uuid not null,
    senior_id uuid not null,
    date_created timestamptz not null default now(),
    is_checked boolean not null default false,
    constraint fk_alert_routine foreign key (routine_id) references routine(id) on delete cascade,
    constraint fk_alert_senior foreign key (senior_id) references users(id) on delete cascade
);

create index ix_alert_senior_created_at on alert_history(senior_id, date_created desc);
create index ix_alert_routine_created_at on alert_history(routine_id, date_created desc);


-- 10) location (등록된 주요 위치)
create table location (
      id uuid primary key default gen_random_uuid(),
      location_name varchar(100) not null,
      senior_id uuid not null,
      guardian_id uuid not null,
      date_created timestamptz not null default now(),
      constraint fk_location_senior foreign key (senior_id) references users(id) on delete cascade,
      constraint fk_location_guardian foreign key (guardian_id) references users(id) on delete cascade
);

create index ix_location_senior on location(senior_id);
create index ix_location_guardian on location(guardian_id);


-- 11) location_log
create table location_log (
      id uuid primary key default gen_random_uuid(),
      senior_id uuid not null,
      location text not null,                 -- "lat,lng" 문자열. (추천: lat/lng 컬럼 분리)
      date_created timestamptz not null default now(),
      constraint fk_location_log_senior foreign key (senior_id) references users(id) on delete cascade
);

create index ix_location_log_senior_time on location_log(senior_id, date_created desc);


-- 12) active_log
create table active_log (
    id uuid primary key default gen_random_uuid(),
    senior_id uuid not null,
    date_created timestamptz not null default now(),
    active_type varchar(50) not null,       -- SCREEN_ON, MOTION, CALL 등
    constraint fk_active_log_senior foreign key (senior_id) references users(id) on delete cascade
);

create index ix_active_log_senior_time on active_log(senior_id, date_created desc);


-- 13) temperature_log
create table temperature_log (
     id uuid primary key default gen_random_uuid(),
     senior_id uuid not null,
     date_created timestamptz not null default now(),
     temperature numeric(5,2) not null,
     location_id uuid,
     constraint fk_temperature_log_senior foreign key (senior_id) references users(id) on delete cascade,
     constraint fk_temperature_log_location foreign key (location_id) references location(id) on delete set null
);

create index ix_temperature_log_senior_time on temperature_log(senior_id, date_created desc);
