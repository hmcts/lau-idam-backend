package uk.gov.hmcts.reform.laubackend.idam.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.laubackend.idam.domain.UserDeletionAudit;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;
import uk.gov.hmcts.reform.laubackend.idam.repository.UserDeletionAuditInsertRepository;
import uk.gov.hmcts.reform.laubackend.idam.repository.UserDeletionAuditRepository;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDeletionAuditService {

    private final TimestampUtil timestampUtil;
    private final UserDeletionAuditInsertRepository userDeletionAuditInsertRepository;
    private final UserDeletionAuditRepository userDeletionAuditRepository;

    @Value("${security.db.backend-encryption-key}")
    private String securityDbBackendEncryptionKey;

    @Value("${security.db.encryption-enabled}")
    private Boolean encryptionEnabled;

    public List<DeletionLog> saveUserDeletion(final List<DeletionLog> deletionLogs) {
        final List<DeletionLog> responses = new ArrayList<>(deletionLogs.size());

        for (DeletionLog log: deletionLogs) {
            final UserDeletionAudit userDeletionAudit = UserDeletionAudit.builder()
                .userId(log.getUserId())
                .emailAddress(log.getEmailAddress())
                .firstName(log.getFirstName())
                .lastName(log.getLastName())
                .timestamp(timestampUtil.getUtcTimestampValue(log.getDeletionTimestamp()))
                .build();

            final UserDeletionAudit userDeletionAuditResponse;

            if (BooleanUtils.isTrue(encryptionEnabled)) {
                userDeletionAuditResponse = userDeletionAuditInsertRepository
                    .saveUserDeleteAuditWithEncryption(userDeletionAudit, securityDbBackendEncryptionKey);
            } else {
                userDeletionAuditResponse = userDeletionAuditRepository.save(userDeletionAudit);
            }
            responses.add(DeletionLog.toDto(userDeletionAuditResponse));
        }
        return responses;
    }
}
