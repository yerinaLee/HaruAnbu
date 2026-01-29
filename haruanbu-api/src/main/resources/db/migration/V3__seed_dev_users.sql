insert into users (id, role, email, created_at)
values
  ('11111111-1111-1111-1111-111111111111', 'GUARDIAN', 'guardian@haruanbu.local', now()),
  ('22222222-2222-2222-2222-222222222222', 'SENIOR',  'senior@haruanbu.local',  now())
on conflict do nothing;
