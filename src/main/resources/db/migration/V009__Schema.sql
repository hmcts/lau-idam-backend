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
