package uk.gov.hmcts.reform.laubackend.idam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.repository.IdamLogonAuditRepository;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogResponse;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

@Service
public class LogonLogService {

    @Autowired
    private IdamLogonAuditRepository idamLogonAuditRepository;

    @Autowired
    private TimestampUtil timestampUtil;

    public LogonLogPostResponse saveLogonLog(final LogonLog logonLog) {

        final IdamLogonAudit idamLogonAudit = new IdamLogonAudit();
        idamLogonAudit.setUserId(logonLog.getUserId());
        idamLogonAudit.setService(logonLog.getService());
        idamLogonAudit.setIpAddress(logonLog.getIpAddress());
        idamLogonAudit.setEmailAddress(logonLog.getEmailAddress());
        idamLogonAudit.setTimestamp(timestampUtil.getUtcTimestampValue(logonLog.getTimestamp()));

        final IdamLogonAudit idamLogonAuditResponse = idamLogonAuditRepository.save(idamLogonAudit);
        final String timestamp = timestampUtil.timestampConvertor(idamLogonAudit.getTimestamp());

        return new LogonLogPostResponse(new LogonLogResponse().toDto(idamLogonAuditResponse, timestamp));
    }
}
