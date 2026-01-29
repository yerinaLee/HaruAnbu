create table group_invites (
  id uuid primary key,
  group_id uuid not null references care_groups(id),
  created_by uuid not null references users(id),
  code varchar(32) not null unique,
  expires_at timestamptz not null,
  used_by uuid references users(id),
  used_at timestamptz
);

create index idx_group_invites_group on group_invites(group_id);
create index idx_group_invites_code on group_invites(code);
