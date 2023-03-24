package uk.gov.hmcts.reform.laubackend.idam.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

@Repository
public class IdamLogonAuditInsertRepository {

    private final String insertLogonAuditQueryWithEncryption =
            "INSERT INTO public.idam_logon_audit "
                    + "(user_id, service, log_timestamp, login_state, ip_address ,email_address, email_address_mac) "
                    + "VALUES (:userId, :service, :logTimestamp, :loginState, "
                    + "encode(pgp_sym_encrypt(cast(:ipAddress as text), cast(:encryptionKey as text)), 'base64'), "
                    + "encode(pgp_sym_encrypt(cast(:emailAddress as text), cast(:encryptionKey as text)), 'base64'), "
                    + "encode(hmac(cast(:emailAddress as text), cast(:encryptionKey as text), 'sha256'), 'hex')) "
                    + "RETURNING id";

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public IdamLogonAuditInsertRepository(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Transactional
    public IdamLogonAudit saveIdamLogonAuditWithEncryption(
            IdamLogonAudit idamLogonAudit,
            String securityDbBackendEncryptionKey) {
        Query insertQuery = entityManager.createNativeQuery(insertLogonAuditQueryWithEncryption);

        insertQuery.setParameter("userId", idamLogonAudit.getUserId())
                .setParameter("service", idamLogonAudit.getService())
                .setParameter("logTimestamp", idamLogonAudit.getTimestamp())
                .setParameter("ipAddress", idamLogonAudit.getIpAddress())
                .setParameter("loginState", idamLogonAudit.getLoginState())
                .setParameter("emailAddress", idamLogonAudit.getEmailAddress())
                .setParameter("encryptionKey", securityDbBackendEncryptionKey);

        Integer generatedId = (Integer) insertQuery.getSingleResult();
        idamLogonAudit.setId(generatedId.longValue());
        return idamLogonAudit;
    }
}
