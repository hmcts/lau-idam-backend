
  -- idam_logon_audit table
  CREATE TABLE idam_logon_audit (
   id SERIAL PRIMARY KEY,
   user_id VARCHAR(64) NOT NULL,
   email_address VARCHAR(256) NOT NULL,
   service VARCHAR(70),
   ip_address VARCHAR(256),
   login_state VARCHAR(64),
   log_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
  );

  -- case_action_audit comments
  comment on column idam_logon_audit.id is 'Unique lau idam id';
  comment on column idam_logon_audit.user_id is 'User id performing the logon action';
  comment on column idam_logon_audit.email_address is 'User email address';
  comment on column idam_logon_audit.service is 'The used service';
  comment on column idam_logon_audit.ip_address is 'User ip address';
  comment on column idam_logon_audit.log_timestamp is 'Logon action timestamp';
  comment on column idam_logon_audit.login_state is 'Idam event type - authorization or authentication';

  -- idam_logon_audit indexes
  CREATE INDEX logon_audit_user_id_idx ON idam_logon_audit (user_id);
  CREATE INDEX logon_audit_email_adr_idx ON idam_logon_audit (email_address);
  CREATE INDEX logon_audit_service_idx ON idam_logon_audit (service);
  CREATE INDEX logon_audit_ip_address_idx ON idam_logon_audit (ip_address);
  CREATE INDEX logon_audit_log_timestamp_idx ON idam_logon_audit (log_timestamp);
  CREATE INDEX logon_audit_event_idx ON idam_logon_audit (login_state);



