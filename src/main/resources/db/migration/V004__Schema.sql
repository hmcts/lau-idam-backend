ALTER TABLE idam_logon_audit ALTER COLUMN email_address TYPE VARCHAR(256);

-- drop extension safely
DO
$do$
BEGIN
  IF EXISTS (
  SELECT FROM pg_extension  -- SELECT list can be empty for this
    WHERE extname = 'citext') THEN
  DROP EXTENSION IF EXISTS citext;
END IF;
exception
  when sqlstate '42501' then
  RAISE NOTICE 'Dropping citext failed.';
END
$do$;
