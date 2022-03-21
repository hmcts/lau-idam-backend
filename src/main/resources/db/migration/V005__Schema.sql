-- alter role for application access safely
DO
$do$
    BEGIN
        IF EXISTS (
                SELECT FROM pg_catalog.pg_roles  -- SELECT list can be empty for this
                WHERE  rolname = 'lauuser') THEN
            ALTER ROLE lauuser LOGIN PASSWORD '${LAU_IDAM_DB_PASSWORD}';
        END IF;
    exception
        WHEN OTHERS THEN
            RAISE NOTICE 'Not enough permissions to alter user.';
    END
$do$;
