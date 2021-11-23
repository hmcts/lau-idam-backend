package uk.gov.hmcts.reform.laubackend.idam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

@SuppressWarnings({"PMD.UseObjectForClearerAPI"})
@Repository
public interface IdamLogonAuditRepository extends JpaRepository<IdamLogonAudit, Long> {
}
