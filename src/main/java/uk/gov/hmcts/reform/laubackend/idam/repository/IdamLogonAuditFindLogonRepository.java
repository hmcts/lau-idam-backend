package uk.gov.hmcts.reform.laubackend.idam.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@SuppressWarnings("unchecked")
@Repository
public class IdamLogonAuditFindLogonRepository {
    private static final String SELECT_CRITERIA = """
        SELECT ila.id, ila.user_id, ila.service, ila.login_state, ila.log_timestamp,
               decrypt_value(ila.email_address, :encryptionKey) as email_address,
               decrypt_value(ila.ip_address, :encryptionKey) as ip_address""";

    private static final String FROM = "FROM idam_logon_audit ila";
    private static final String WHERE = "WHERE";
    private static final String TIMESTAMP_START_CRITERIA = "ila.log_timestamp >= :startTime";
    private static final String TIMESTAMP_END_CRITERIA = "ila.log_timestamp <= :endTime";
    private static final String USER_ID_CRITERIA = "ila.user_id = :userId";
    private static final String EMAIL_CRITERIA = """
        encode(hmac(:emailAddress, :encryptionKey , 'sha256'), 'hex')
        = ila.email_address_mac""";

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public IdamLogonAuditFindLogonRepository(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public Page<IdamLogonAudit> findIdamLogon(final String userId,
                                              final String emailAddress,
                                              final Timestamp startTime,
                                              final Timestamp endTime,
                                              final String encryptionKey,
                                              final Pageable pageable) {
        final List<String> queryParts = new LinkedList<>();

        queryParts.add(SELECT_CRITERIA);
        queryParts.add(FROM);

        addSearchCriteria(queryParts, userId, emailAddress, startTime, endTime);

        final String queryString = String.join(" ", queryParts);
        final Query query = entityManager.createNativeQuery(queryString, IdamLogonAudit.class);
        setSearchParams(query, startTime, endTime, userId, emailAddress);
        query.setParameter("encryptionKey", encryptionKey);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        final List<IdamLogonAudit> results = query.getResultList();
        long totalCount = countResults(userId, emailAddress, encryptionKey, startTime, endTime);

        return new PageImpl<>(results, pageable, totalCount);
    }

    private long countResults(final String userId,
                              final String emailAddress,
                              final String encryptionKey,
                              final Timestamp startTime,
                              final Timestamp endTime) {

        final List<String> queryParts = new LinkedList<>();
        queryParts.add("SELECT count(*) FROM (");
        queryParts.add("SELECT 1");
        queryParts.add(FROM);

        addSearchCriteria(queryParts, userId, emailAddress, startTime, endTime);

        queryParts.add("limit 10000) ila");
        final String queryString = String.join(" ", queryParts);
        final Query countQuery = entityManager.createNativeQuery(queryString);
        setSearchParams(countQuery, startTime, endTime, userId, emailAddress);
        if (!isEmpty(emailAddress)) {
            countQuery.setParameter("encryptionKey", encryptionKey);
        }
        return ((Number) countQuery.getSingleResult()).intValue();
    }

    private void addSearchCriteria(List<String> queryParts,
                                   String userId,
                                   String emailAddress,
                                   Timestamp startTime,
                                   Timestamp endTime) {
        List<String> searchParts = createSearchCriteria(userId, emailAddress, startTime, endTime);
        if (!searchParts.isEmpty()) {
            queryParts.add(WHERE);
            queryParts.add(String.join(" AND ", searchParts));
        }
    }


    private List<String> createSearchCriteria(final String userId,
                                              final String emailAddress,
                                              final Timestamp startTime,
                                              final Timestamp endTime) {
        final List<String> criteria = new LinkedList<>();
        if (!isEmpty(userId)) {
            criteria.add(USER_ID_CRITERIA);
        }

        if (!isEmpty(emailAddress)) {
            criteria.add(EMAIL_CRITERIA);
        }

        if (startTime != null) {
            criteria.add(TIMESTAMP_START_CRITERIA);
        }

        if (endTime != null) {
            criteria.add(TIMESTAMP_END_CRITERIA);
        }
        return criteria;
    }

    private void setSearchParams(final Query query,
                                 final Timestamp startTime,
                                 final Timestamp endTime,
                                 final String userId,
                                 final String emailAddress) {
        if (startTime != null) {
            query.setParameter("startTime", startTime);
        }

        if (endTime != null) {
            query.setParameter("endTime", endTime);
        }

        if (!isEmpty(userId)) {
            query.setParameter("userId", userId);
        }
        if (!isEmpty(emailAddress)) {
            query.setParameter("emailAddress", emailAddress);
        }
    }

}
