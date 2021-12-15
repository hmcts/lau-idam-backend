CREATE EXTENSION IF NOT EXISTS citext;

ALTER TABLE idam_logon_audit ALTER COLUMN email_address TYPE citext;