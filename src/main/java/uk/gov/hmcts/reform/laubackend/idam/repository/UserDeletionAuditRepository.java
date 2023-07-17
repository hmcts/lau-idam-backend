package uk.gov.hmcts.reform.laubackend.idam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.hmcts.reform.laubackend.idam.domain.UserDeletionAudit;

@Repository
public interface UserDeletionAuditRepository extends JpaRepository<UserDeletionAudit, Long> {

    boolean existsUserDeletionAuditByUserId(String userId);

    void deleteUserDeletionAuditByUserId(String userId);
}
