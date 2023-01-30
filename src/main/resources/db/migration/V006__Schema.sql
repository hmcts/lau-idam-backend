ALTER TABLE idam_logon_audit ALTER COLUMN service DROP NOT NULL;
ALTER TABLE idam_logon_audit ADD COLUMN login_state VARCHAR(16) CHECK (login_state in ('AUTHENTICATE','AUTHORIZE'));

comment on column idam_logon_audit.login_state is 'Idam event type - authorization or authentication';

CREATE INDEX logon_audit_event_idx ON idam_logon_audit (login_state);