package uk.gov.hmcts.reform.laubackend.idam.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

@Repository
public class IdamLogonAuditInsertRepository {

    private static final String INSERT_QUERY_WITH_ENCRYPTION = """
        INSERT INTO public.idam_logon_audit
            (user_id, service, log_timestamp, login_state, ip_address ,email_address, email_address_mac)
        VALUES (:userId, :service, :logTimestamp, :loginState,
            encode(pgp_sym_encrypt(cast(:ipAddress as text), cast(:encryptionKey as text)), 'base64'),
            encode(pgp_sym_encrypt(cast(:emailAddress as text), cast(:encryptionKey as text)), 'base64'),
            encode(hmac(cast(:emailAddress as text), cast(:encryptionKey as text), 'sha256'), 'hex'))
        RETURNING id;
        """;

    @PersistenceContext
    private final EntityManager entityManager;

    public IdamLogonAuditInsertRepository(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public IdamLogonAudit saveIdamLogonAuditWithEncryption(
            IdamLogonAudit idamLogonAudit,
            String securityDbBackendEncryptionKey) {
        Query insertQuery = entityManager.createNativeQuery(INSERT_QUERY_WITH_ENCRYPTION);

        insertQuery.setParameter("userId", idamLogonAudit.getUserId())
                .setParameter("service", idamLogonAudit.getService())
                .setParameter("logTimestamp", idamLogonAudit.getTimestamp())
                .setParameter("ipAddress", idamLogonAudit.getIpAddress())
                .setParameter("loginState", idamLogonAudit.getLoginState())
                .setParameter("emailAddress", idamLogonAudit.getEmailAddress())
                .setParameter("encryptionKey", securityDbBackendEncryptionKey);

        long generatedId = ((Number) insertQuery.getSingleResult()).longValue();
        idamLogonAudit.setId(generatedId);

        return idamLogonAudit;
    }
}
