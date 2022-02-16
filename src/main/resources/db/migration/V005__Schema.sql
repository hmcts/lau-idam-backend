UPDATE idam_logon_audit ila
SET email_address_mac = encode(hmac(ila2.email, '${LAU_IDAM_ENCRYPTION_KEY}', 'sha256'), 'hex')
FROM (SELECT id, pgp_sym_decrypt(decode(ila2.email_address, 'base64'), '${LAU_IDAM_ENCRYPTION_KEY}') AS email
      FROM idam_logon_audit ila2) ila2
WHERE ila.id = ila2.id;
