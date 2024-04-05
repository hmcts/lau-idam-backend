package uk.gov.hmcts.reform.laubackend.idam.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.laubackend.idam.domain.UserDeletionAudit;

@Repository
@RequiredArgsConstructor
public class UserDeletionAuditInsertRepository {

    private static final String INSERT_QUERY = """
        INSERT INTO public.user_deletion_audit (
            user_id,
            email_address,
            email_address_hmac,
            first_name,
            first_name_hmac,
            last_name,
            last_name_hmac,
            deletion_timestamp
        )
        VALUES (
            :userId,
            encrypt_value(TRIM(:emailAddress), :encryptionKey),
            hash_value(TRIM(LOWER(:emailAddress)), :encryptionKey),
            encrypt_value(TRIM(:firstName), :encryptionKey),
            hash_value(TRIM(LOWER(:firstName)), :encryptionKey),
            encrypt_value(TRIM(:lastName), :encryptionKey),
            hash_value(TRIM(LOWER(:lastName)), :encryptionKey),
            :deletionTimestamp
        ) RETURNING id
        """;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public UserDeletionAudit saveUserDeleteAuditWithEncryption(
        UserDeletionAudit userDeletionAudit,
        String securityDbBackendEncryptionKey) {
        Query insertQuery = entityManager.createNativeQuery(INSERT_QUERY);

        insertQuery.setParameter("userId", userDeletionAudit.getUserId())
            .setParameter("emailAddress", userDeletionAudit.getEmailAddress())
            .setParameter("firstName", userDeletionAudit.getFirstName())
            .setParameter("lastName", userDeletionAudit.getLastName())
            .setParameter("deletionTimestamp", userDeletionAudit.getTimestamp())
            .setParameter("encryptionKey", securityDbBackendEncryptionKey);

        long generatedId = ((Number) insertQuery.getSingleResult()).longValue();
        userDeletionAudit.setId(generatedId);

        return userDeletionAudit;
    }

}
