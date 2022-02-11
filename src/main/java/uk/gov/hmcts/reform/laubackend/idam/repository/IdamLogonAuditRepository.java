package uk.gov.hmcts.reform.laubackend.idam.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

import java.sql.Timestamp;

@SuppressWarnings({"PMD.UseObjectForClearerAPI"})
@Repository
public interface IdamLogonAuditRepository extends JpaRepository<IdamLogonAudit, Long> {

    @Query(value = "SELECT * FROM idam_logon_audit ila "
            + "WHERE (cast(:userId as text) IS NULL OR ila.user_id=cast(:userId as text)) "
            + "AND (cast(:emailAddress as text) IS NULL OR ila.email_address = cast(:emailAddress as text)) "
            + "AND (cast(cast(:startTime as varchar) as timestamp) IS NULL "
            + "OR ila.log_timestamp >= cast(cast(:startTime as varchar) as timestamp)) "
            + "AND (cast(cast(:endTime as varchar) as timestamp) IS NULL "
            + "OR ila.log_timestamp <= cast(cast(:endTime as varchar) as timestamp)) ",
            countQuery = "SELECT count(*) FROM ( "
                + "SELECT 1 FROM idam_logon_audit ca "
                + "WHERE (cast(:userId as text) IS NULL OR ila.user_id=cast(:userId as text)) "
                + "AND (cast(:emailAddress as text) IS NULL OR ila.email_address = cast(:emailAddress as text)) "
                + "AND (cast(cast(:startTime as varchar) as timestamp) IS NULL "
                + "OR ila.log_timestamp >= cast(cast(:startTime as varchar) as timestamp)) "
                + "AND (cast(cast(:endTime as varchar) as timestamp) IS NULL "
                + "OR ila.log_timestamp <= cast(cast(:endTime as varchar) as timestamp)) "
                + "limit 100000) ila",
            nativeQuery = true)
    Page<IdamLogonAudit> findIdamLogon(final @Param("userId") String userId,
                                       final @Param("emailAddress") String emailAddress,
                                       final @Param("startTime") Timestamp startTime,
                                       final @Param("endTime") Timestamp endTime,
                                       final Pageable pageable);
}
