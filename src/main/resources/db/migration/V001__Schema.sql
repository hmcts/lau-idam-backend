
  -- idam_logon_audit table
  CREATE TABLE idam_logon_audit (
   id SERIAL PRIMARY KEY,
   user_id VARCHAR(64) NOT NULL,
   email_address TEXT NOT NULL,
   service VARCHAR(70) NOT NULL,
   ip_address TEXT NOT NULL,
   log_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
  );

  -- case_action_audit comments
  comment on column idam_logon_audit.id is 'Unique lau idam id';
  comment on column idam_logon_audit.user_id is 'User id performing the logon action';
  comment on column idam_logon_audit.email_address is 'User email address';
  comment on column idam_logon_audit.service is 'The used service';
  comment on column idam_logon_audit.ip_address is 'User ip address';
  comment on column idam_logon_audit.log_timestamp is 'Logon action timestamp';

  -- idam_logon_audit indexes
  CREATE INDEX logon_audit_user_id_idx ON idam_logon_audit (user_id);
  CREATE INDEX logon_audit_email_adr_idx ON idam_logon_audit (email_address);
  CREATE INDEX logon_audit_service_idx ON idam_logon_audit (service);
  CREATE INDEX logon_audit_ip_address_idx ON idam_logon_audit (ip_address);
  CREATE INDEX logon_audit_log_timestamp_idx ON idam_logon_audit (log_timestamp);


  -- create user for application access
  DO
  $do$
  BEGIN
     IF NOT EXISTS (
        SELECT FROM pg_catalog.pg_roles  -- SELECT list can be empty for this
        WHERE  rolname = 'lauuser') THEN
        CREATE ROLE lauuser LOGIN PASSWORD '${LAU_IDAM_DB_PASSWORD}';
     END IF;
  END
  $do$;
--
  GRANT USAGE, SELECT ON SEQUENCE idam_logon_audit_id_seq TO lauuser;
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE idam_logon_audit, flyway_schema_history TO lauuser;
  CREATE EXTENSION IF NOT EXISTS pgcrypto;
