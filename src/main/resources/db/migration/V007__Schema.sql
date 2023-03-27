CREATE OR REPLACE FUNCTION decrypt_value(
    encrypted_value text,
    encryption_key text
)
RETURNS text AS $$
BEGIN
RETURN pgp_sym_decrypt(decode(encrypted_value, 'base64'), encryption_key);
END;
$$ LANGUAGE plpgsql;