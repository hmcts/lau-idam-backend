package uk.gov.hmcts.reform.laubackend.idam.repository;

import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Repository
public class IdamLogonAuditInsertRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final String insertLogonAuditQueryWithEncryption =
            "INSERT INTO public.idam_logon_audit "
                    + "(user_id, service, log_timestamp, ip_address, email_address, email_address_mac) "
                    + "VALUES (:userId, :service, :logTimestamp, "
                    + "encode(pgp_sym_encrypt(:ipAddress, :encryptionKey), 'base64'), "
                    + "encode(pgp_sym_encrypt(:emailAddress, :encryptionKey), 'base64'), "
                    + "encode(hmac(:emailAddress, :encryptionKey, 'sha256'), 'hex')) "
                    + "RETURNING id";

    @Transactional
    public IdamLogonAudit saveIdamLogonAuditWithEncryption(
            IdamLogonAudit idamLogonAudit,
            String securityDbBackendEncryptionKey) {
        Query insertQuery = entityManager.createNativeQuery(insertLogonAuditQueryWithEncryption);

        insertQuery.setParameter("userId", idamLogonAudit.getUserId())
                .setParameter("service", idamLogonAudit.getService())
                .setParameter("logTimestamp", idamLogonAudit.getTimestamp())
                .setParameter("ipAddress", idamLogonAudit.getIpAddress())
                .setParameter("emailAddress", idamLogonAudit.getEmailAddress())
                .setParameter("encryptionKey", securityDbBackendEncryptionKey);

        Integer generatedId = (Integer) insertQuery.getSingleResult();
        idamLogonAudit.setId(generatedId.longValue());
        return idamLogonAudit;
    }
}
