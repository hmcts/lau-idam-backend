-- Drop email address index
DROP INDEX logon_audit_email_adr_idx;

--  Add column email_address_mac
ALTER TABLE idam_logon_audit ADD COLUMN email_address_mac text;
CREATE INDEX idam_logon_audit_enc_str1_idx ON idam_logon_audit (email_address_mac);

-- Comments case_search_audit for new column email_address_mac
comment on column idam_logon_audit.email_address_mac is 'Encoded email search string';

-- Update script to copy existing email addresses values over
UPDATE idam_logon_audit ila
SET email_address_mac = encode(hmac(ila2.email, '${LAU_IDAM_ENCRYPTION_KEY}', 'sha256'), 'hex')
FROM (SELECT id, pgp_sym_decrypt(decode(ila2.email_address, 'base64'), '${LAU_IDAM_ENCRYPTION_KEY}') AS email
      FROM idam_logon_audit ila2) ila2
WHERE ila.id = ila2.id;
