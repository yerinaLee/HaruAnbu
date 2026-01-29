create table users (
  id uuid primary key,
  role varchar(20) not null,
  email varchar(255) unique,
  phone varchar(50) unique,
  password_hash varchar(255),
  created_at timestamptz not null default now()
);

create table care_groups (
  id uuid primary key,
  name varchar(100) not null,
  created_by uuid not null references users(id),
  created_at timestamptz not null default now()
);

create table care_group_members (
  group_id uuid not null references care_groups(id),
  user_id uuid not null references users(id),
  member_role varchar(20) not null,
  permissions jsonb not null default '{}'::jsonb,
  created_at timestamptz not null default now(),
  primary key (group_id, user_id)
);
