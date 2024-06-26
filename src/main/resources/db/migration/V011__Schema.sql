ALTER TABLE user_deletion_audit
  RENAME TO user_deletion_audit_old;

ALTER SEQUENCE user_deletion_audit_id_seq
  RENAME TO user_deletion_audit_id_old_seq;

ALTER INDEX deletion_audit_user_id_idx
  RENAME TO deletion_audit_user_id_old_idx;

ALTER INDEX deletion_audit_email_adr_idx
  RENAME TO deletion_audit_email_adr_old_idx;

ALTER INDEX deletion_audit_first_name_idx
  RENAME TO deletion_audit_first_name_old_idx;

ALTER INDEX deletion_audit_last_name_idx
  RENAME TO deletion_audit_last_name_old_idx;

ALTER INDEX deletion_audit_timestamp_idx
  RENAME TO deletion_audit_timestamp_old_idx;

CREATE TABLE user_deletion_audit (
  id SERIAL PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  email_address TEXT NOT NULL,
  email_address_hmac TEXT NOT NULL,
  first_name TEXT NOT NULL,
  first_name_hmac TEXT NOT NULL,
  last_name TEXT NOT NULL,
  last_name_hmac TEXT NOT NULL,
  deletion_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- user_deletion_audit comments
comment on column user_deletion_audit.id is 'Unique user deletion entry id';
comment on column user_deletion_audit.user_id is 'User id performing the deletion action';
comment on column user_deletion_audit.email_address is 'Encrypted user email address';
comment on column user_deletion_audit.email_address_hmac is 'User email address for searching purposes';
comment on column user_deletion_audit.first_name is 'Encrypted user first name';
comment on column user_deletion_audit.first_name_hmac is 'User first name for searching purposes';
comment on column user_deletion_audit.last_name is 'Encrypted user last name';
comment on column user_deletion_audit.last_name_hmac is 'User last name for searching purposes';
comment on column user_deletion_audit.deletion_timestamp is 'Deletion action timestamp';

-- user_deletion_audit indexes
CREATE INDEX deletion_audit_user_id_idx ON user_deletion_audit (user_id);
CREATE INDEX deletion_audit_email_adr_idx ON user_deletion_audit (email_address_hmac);
CREATE INDEX deletion_audit_first_name_idx ON user_deletion_audit (first_name_hmac);
CREATE INDEX deletion_audit_last_name_idx ON user_deletion_audit (last_name_hmac);
CREATE INDEX deletion_audit_timestamp_idx ON user_deletion_audit (deletion_timestamp);

-- grant usage to user
GRANT USAGE, SELECT ON SEQUENCE user_deletion_audit_id_seq TO lauuser;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE user_deletion_audit TO lauuser;
