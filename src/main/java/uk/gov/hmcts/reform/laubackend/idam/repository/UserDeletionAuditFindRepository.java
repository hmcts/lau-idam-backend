package uk.gov.hmcts.reform.laubackend.idam.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.laubackend.idam.domain.UserDeletionAudit;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogAllUsersRequestParams;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogGetRequestParams;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class UserDeletionAuditFindRepository {

    private final TimestampUtil timestampUtil;

    private static final int MAX_RESULT_COUNT = 10_000;

    private static final String SELECT = """
        SELECT
            id,
            user_id,
            decrypt_value(email_address, :encryptionKey) as email_address,
            decrypt_value(first_name, :encryptionKey) as first_name,
            decrypt_value(last_name, :encryptionKey) as last_name,
            deletion_timestamp FROM user_deletion_audit
        """;

    private static final String WHERE = "WHERE";
    private static final String TIME_RANGE_CRITERIA =
        "deletion_timestamp >= :startTime AND deletion_timestamp <= :endTime";

    private static final String USER_CRITERIA = "AND user_id = :userId";
    private static final String EMAIL_CRITERIA = "AND email_address_hmac = hash_value(:emailAddress, :encryptionKey)";
    private static final String FIRST_NAME_CRITERIA = "AND first_name_hmac = hash_value(:firstName, :encryptionKey)";
    private static final String LAST_NAME_CRITERIA = "AND last_name_hmac = hash_value(:lastName, :encryptionKey)";
    private static final String ORDER = "ORDER by deletion_timestamp";
    private static final String DESC = "DESC";

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<UserDeletionAudit> findUserDeletion(
        final DeletionLogGetRequestParams params,
        final String encryptionKey,
        final Pageable pageable
    ) {

        final List<String> queryParts = new LinkedList<>();
        queryParts.add(SELECT);
        queryParts.add(WHERE);
        queryParts.add(TIME_RANGE_CRITERIA);

        Map<String, String> usedParams = addSearchCriteria(queryParts, params);
        queryParts.add(ORDER);
        queryParts.add(DESC);

        final String queryString = String.join(" ", queryParts);
        final Query query = entityManager.createNativeQuery(queryString, UserDeletionAudit.class);
        final Timestamp startTime = timestampUtil.getTimestampValue(params.startTimestamp());
        final Timestamp endTime = timestampUtil.getTimestampValue(params.endTimestamp());
        setQueryParams(query, usedParams, startTime, endTime);
        query.setParameter("encryptionKey", encryptionKey);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        final List<UserDeletionAudit> results = query.getResultList();

        long totalCount = countResults(params, startTime, endTime,  encryptionKey);

        return new PageImpl<>(results, pageable, totalCount);
    }

    private long countResults(final DeletionLogGetRequestParams params,
                              final Timestamp startTime,
                              final Timestamp endTime,
                              final String encryptionKey) {

        final List<String> clauses = new LinkedList<>();

        String fullQuery = "SELECT count(*) FROM (%s) AS tbl";
        String innerQuery = "SELECT 1 FROM user_deletion_audit WHERE %s LIMIT %s";

        clauses.add(TIME_RANGE_CRITERIA);
        Map<String, String> usedParams = addSearchCriteria(clauses, params);
        innerQuery = String.format(innerQuery, String.join(" ", clauses), MAX_RESULT_COUNT);
        fullQuery = String.format(fullQuery, innerQuery);
        final Query query = entityManager.createNativeQuery(fullQuery);
        setQueryParams(query, usedParams, startTime, endTime);

        List<String> fieldsWithEncryption = Arrays.asList("emailAddress", "firstName", "lastName");
        boolean requireEncryptionKey = fieldsWithEncryption.stream().anyMatch(usedParams::containsKey);

        if (requireEncryptionKey) {
            query.setParameter("encryptionKey", encryptionKey);
        }

        return ((Number) query.getSingleResult()).intValue();
    }

    private void setQueryParams(final Query query,
                                final Map<String, String> usedParams,
                                final Timestamp startTime,
                                final Timestamp endTime) {

        usedParams.forEach(query::setParameter);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);
    }

    private Map<String, String> addSearchCriteria(
        final List<String> queryParts,
        final DeletionLogGetRequestParams params
    ) {
        Map<String, String> usedParams = new ConcurrentHashMap<>();
        if (params == null) {
            return usedParams;
        }
        if (!StringUtils.isEmpty(params.userId())) {
            queryParts.add(USER_CRITERIA);
            usedParams.put("userId", params.userId());
        }
        if (!StringUtils.isEmpty(params.emailAddress())) {
            queryParts.add(EMAIL_CRITERIA);
            usedParams.put("emailAddress", params.emailAddress());
        }
        if (!StringUtils.isEmpty(params.firstName())) {
            queryParts.add(FIRST_NAME_CRITERIA);
            usedParams.put("firstName", params.firstName());
        }
        if (!StringUtils.isEmpty(params.lastName())) {
            queryParts.add(LAST_NAME_CRITERIA);
            usedParams.put("lastName", params.lastName());
        }
        return usedParams;
    }

    @Transactional(readOnly = true)
    public Page<UserDeletionAudit> findAllDeletedUsers(
        final DeletionLogAllUsersRequestParams params,
        final String encryptionKey,
        final Pageable pageable
    ) {

        final List<String> queryParts = new LinkedList<>();
        queryParts.add(SELECT);
        queryParts.add(ORDER);
        queryParts.add(getSort(params));

        final String queryString = String.join(" ", queryParts);
        final Query query = entityManager.createNativeQuery(queryString, UserDeletionAudit.class);
        query.setParameter("encryptionKey", encryptionKey);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        final List<UserDeletionAudit> results = query.getResultList();

        long totalCount = countTotalDeletedUsers();

        return new PageImpl<>(results, pageable, totalCount);
    }

    private long countTotalDeletedUsers() {
        String fullQuery = "SELECT count(*) FROM user_deletion_audit";
        final Query query = entityManager.createNativeQuery(fullQuery);

        return ((Number) query.getSingleResult()).intValue();
    }

    String getSort(DeletionLogAllUsersRequestParams params) {
        return params.sort() == null || params.sort().length() == 0  ? DESC : params.sort();
    }


}
