CREATE TABLE email_verification (
    id uuid primary key default gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    verification_code VARCHAR(255) NOT NULL,
    is_sent BOOLEAN NOT NULL DEFAULT FALSE,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    date_created TIMESTAMP NOT NULL DEFAULT now(),
    last_updated TIMESTAMP NOT NULL DEFAULT now(),
    attempt_count int not null default 0, -- 재발송 횟수
    expires_at timestamptz not null
);

CREATE INDEX index_01
    ON email_verification (email);

CREATE INDEX index_02
    ON email_verification (email, verification_code);

CREATE INDEX verificationcodeindex
    ON email_verification (verification_code);
