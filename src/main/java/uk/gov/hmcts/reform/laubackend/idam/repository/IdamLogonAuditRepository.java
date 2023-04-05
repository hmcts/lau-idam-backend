package uk.gov.hmcts.reform.laubackend.idam.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

import java.sql.Timestamp;

@SuppressWarnings({"PMD.UseObjectForClearerAPI", "PMD.AvoidDuplicateLiterals","PMD.FinalParameterInAbstractMethod"})
@Repository
public interface IdamLogonAuditRepository extends JpaRepository<IdamLogonAudit, Long> {

    @Query(value = "SELECT ila.id, ila.user_id, ila.service, ila.login_state, ila.log_timestamp, "
        + "decrypt_value(ila.email_address, :encryptionKey) as email_address, "
        + "decrypt_value(ila.ip_address, :encryptionKey) as ip_address "
        + "FROM idam_logon_audit ila "
        + "WHERE ila.user_id = COALESCE(:userId, ila.user_id) "
        + "AND (:emailAddress IS NULL "
        + "OR encode(hmac(:emailAddress, "
        + ":encryptionKey , 'sha256'),'hex')=ila.email_address_mac) "
        + "AND ila.log_timestamp >= :startTime "
        + "AND ila.log_timestamp <= :endTime ",
        countQuery = "SELECT count(*) FROM ( "
            + "SELECT 1 FROM idam_logon_audit ila "
            + "WHERE ila.user_id = COALESCE(:userId, ila.user_id) "
            + "AND (:emailAddress IS NULL "
            + "OR encode(hmac(:emailAddress, "
            + ":encryptionKey , 'sha256'),'hex')=ila.email_address_mac) "
            + "AND ila.log_timestamp >= :startTime "
            + "AND ila.log_timestamp <= :endTime "
            + "limit 10000) ila",
        nativeQuery = true)
    Page<IdamLogonAudit> findIdamLogon(final @Param("userId") String userId,
                                       final @Param("emailAddress") String emailAddress,
                                       final @Param("startTime") Timestamp startTime,
                                       final @Param("endTime") Timestamp endTime,
                                       final @Param("encryptionKey") String encryptionKey,
                                       final Pageable pageable);

}
