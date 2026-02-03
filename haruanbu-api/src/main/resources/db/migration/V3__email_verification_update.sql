alter table email_verification
    add column if not exists verified_at timestamptz; -- 이메일 인증 시간대

-- email은 1행만 유지
create unique index if not exists ux_email_verification_email
    on email_verification(email);