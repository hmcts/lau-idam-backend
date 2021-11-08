#!/usr/bin/env bash

set -e

if [ -z "$LAU_IDAM_DB_NAME" ] || [ -z "$LAU_IDAM_DB_USERNAME" ] || [ -z "$LAU_IDAM_DB_PASSWORD" ]; then
  echo "ERROR: Missing environment variable. Set value for 'LAU_IDAM_DB_NAME', 'LAU_IDAM_DB_USERNAME' and 'LAU_IDAM_DB_PASSWORD'."
  exit 1
fi

# Create roles and databases
psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=$LAU_IDAM_DB_USERNAME --set PASSWORD=$LAU_IDAM_DB_PASSWORD <<-EOSQL
  CREATE USER :USERNAME WITH PASSWORD ':PASSWORD';
EOSQL

psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=$LAU_IDAM_DB_USERNAME --set PASSWORD=$LAU_IDAM_DB_PASSWORD --set DATABASE=$LAU_IDAM_DB_NAME <<-EOSQL
  CREATE DATABASE :DATABASE
    WITH OWNER = :USERNAME
    ENCODING = 'UTF-8'
    CONNECTION LIMIT = -1;
    ALTER SCHEMA public OWNER TO :USERNAME;
EOSQL

psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=$LAU_IDAM_DB_USERNAME --set PASSWORD=$LAU_IDAM_DB_PASSWORD <<-EOSQL
  CREATE EXTENSION IF NOT EXISTS pgcrypto;
EOSQL

psql -v ON_ERROR_STOP=1 --username $LAU_IDAM_DB_USERNAME $LAU_IDAM_DB_NAME <<-EOSQL

-- idam_logons_audit table
  CREATE TABLE idam_logons_audit (
   id SERIAL PRIMARY KEY,
   user_id VARCHAR(64) NOT NULL,
   email_address VARCHAR(70) NOT NULL,
   service VARCHAR(70) NOT NULL,
   ip_address VARCHAR(70) NOT NULL,
   log_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
  );

  -- case_action_audit comments
  comment on column idam_logons_audit.id is 'Unique lau idam id';
  comment on column idam_logons_audit.user_id is 'User id performing the logon action';
  comment on column idam_logons_audit.email_address is 'User email address';
  comment on column idam_logons_audit.service is 'The used service';
  comment on column idam_logons_audit.ip_address is 'User ip address';
  comment on column idam_logons_audit.log_timestamp is 'Logon action timestamp';

  -- idam_logons_audit indexes
  CREATE INDEX idam_logons_audit_user_id_idx ON idam_logons_audit (user_id);
  CREATE INDEX idam_logons_audit_email_adr_idx ON idam_logons_audit (email_address);
  CREATE INDEX idam_logons_audit_service_idx ON idam_logons_audit (service);
  CREATE INDEX idam_logons_audit_ip_address_idx ON idam_logons_audit (ip_address);
  CREATE INDEX idam_logons_audit_log_timestamp_idx ON idam_logons_audit (log_timestamp);

  CREATE USER lauuser WITH ENCRYPTED PASSWORD 'laupass';
  GRANT USAGE, SELECT ON SEQUENCE idam_logons_audit_id_seq TO lauuser;
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE logon_audit, flyway_schema_history TO lauuser;

EOSQL
  echo "Database $service: Created"