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

    @Query("SELECT ila FROM idam_logon_audit ila "
        + "WHERE (:userId IS NULL OR ila.userId = :userId) "
        + "AND (:emailAddress IS NULL OR ila.emailAddress LIKE :emailAddress) "
        + "AND (cast(:startTime as timestamp) IS NULL OR ila.timestamp >= :startTime) "
        + "AND (cast(:endTime as timestamp) IS NULL OR ila.timestamp <= :endTime)")
    Page<IdamLogonAudit> findIdamLogon(final @Param("userId") String userId,
                                       final @Param("emailAddress") String emailAddress,
                                       final @Param("startTime") Timestamp startTime,
                                       final @Param("endTime") Timestamp endTime,
                                       final Pageable pageable);
}
