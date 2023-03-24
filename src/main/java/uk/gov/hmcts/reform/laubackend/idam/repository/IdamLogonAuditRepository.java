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
            + "pgp_sym_decrypt(decode(ila.email_address, 'base64'), cast(:encryptionKey as text)) as email_address, "
            + "pgp_sym_decrypt(decode(ila.ip_address, 'base64'), cast(:encryptionKey as text)) as ip_address "
            + "FROM idam_logon_audit ila "
            + "WHERE ila.user_id = COALESCE(cast(:userId as text), ila.user_id) "
            + "AND (cast(:emailAddress as text) IS NULL "
            + "OR encode(hmac(cast(:emailAddress as text), "
            + "cast(:encryptionKey as text), 'sha256'),'hex')=ila.email_address_mac) "
            + "AND ila.log_timestamp >= COALESCE(cast(:startTime as timestamp), ila.log_timestamp) "
            + "AND ila.log_timestamp <= COALESCE(cast(:endTime as timestamp), ila.log_timestamp)",
            countQuery = "SELECT count(*) FROM ( "
                    + "SELECT 1 FROM idam_logon_audit ila "
                    + "WHERE ila.user_id = COALESCE(cast(:userId as text), ila.user_id) "
                    + "AND (cast(:emailAddress as text) IS NULL "
                    + "OR encode(hmac(cast(:emailAddress as text), "
                    + "cast(:encryptionKey as text), 'sha256'),'hex')=ila.email_address_mac) "
                    + "AND ila.log_timestamp >= COALESCE(cast(:startTime as timestamp), ila.log_timestamp) "
                    + "AND ila.log_timestamp <= COALESCE(cast(:endTime as timestamp), ila.log_timestamp)"
                    + "limit 10000) ila",
            nativeQuery = true)
    Page<IdamLogonAudit> findIdamLogon(final @Param("userId") String userId,
                                       final @Param("emailAddress") String emailAddress,
                                       final @Param("startTime") Timestamp startTime,
                                       final @Param("endTime") Timestamp endTime,
                                       final @Param("encryptionKey") String encryptionKey,
                                       final Pageable pageable);

}
