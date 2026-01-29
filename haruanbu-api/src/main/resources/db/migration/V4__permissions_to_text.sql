alter table care_group_members
alter column permissions type text
  using permissions::text;

alter table care_group_members
    alter column permissions set default '{}';
