package uk.gov.hmcts.reform.laubackend.idam.service;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.repository.IdamLogonAuditInsertRepository;
import uk.gov.hmcts.reform.laubackend.idam.repository.IdamLogonAuditRepository;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogGetResponse;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogResponse;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.springframework.data.domain.PageRequest.of;
import static uk.gov.hmcts.reform.laubackend.idam.response.LogonLogGetResponse.logonLogResponse;

@Service
public class LogonLogService {

    @Autowired
    private IdamLogonAuditRepository idamLogonAuditRepository;

    @Autowired
    private IdamLogonAuditInsertRepository idamLogonAuditInsertRepository;

    @Autowired
    private TimestampUtil timestampUtil;

    @Value("${default.page.size}")
    private String defaultPageSize;

    @Value("${security.db.backend-encryption-key}")
    private String securityDbBackendEncryptionKey;

    @Value("${security.db.encryption-enabled}")
    private Boolean encryptionEnabled;

    public LogonLogGetResponse getLogonLog(final LogonInputParamsHolder inputParamsHolder) {

        final Page<IdamLogonAudit> logonLog = idamLogonAuditRepository.findIdamLogon(
                inputParamsHolder.getUserId(),
                lowerCase(inputParamsHolder.getEmailAddress()),
                timestampUtil.getTimestampValue(inputParamsHolder.getStartTime()),
                timestampUtil.getTimestampValue(inputParamsHolder.getEndTime()),
                securityDbBackendEncryptionKey,
                getPage(inputParamsHolder.getSize(), inputParamsHolder.getPage())
        );

        final List<LogonLog> logonLogList = new ArrayList<>();

        logonLog.getContent().forEach(logonLogAudit -> {
            final String timestamp = timestampUtil.timestampConvertor(logonLogAudit.getTimestamp());
            logonLogList.add(
                    new LogonLog().toDto(logonLogAudit, timestamp)
            );
        });

        return logonLogResponse()
                .withLogonLog(logonLogList)
                .withMoreRecords(logonLog.hasNext())
                .withStartRecordNumber(calculateStartRecordNumber(logonLog))
                .withTotalNumberOfRecords(logonLog.getTotalElements())
                .build();
    }

    public LogonLogPostResponse saveLogonLog(final LogonLog logonLog) {

        final IdamLogonAudit idamLogonAudit = new IdamLogonAudit();
        final IdamLogonAudit idamLogonAuditResponse;
        idamLogonAudit.setUserId(logonLog.getUserId());
        idamLogonAudit.setService(logonLog.getService());
        idamLogonAudit.setIpAddress(logonLog.getIpAddress());
        idamLogonAudit.setEmailAddress(lowerCase(logonLog.getEmailAddress()));
        idamLogonAudit.setTimestamp(timestampUtil.getUtcTimestampValue(logonLog.getTimestamp()));

        if (BooleanUtils.isTrue(encryptionEnabled)) {
            idamLogonAuditResponse = idamLogonAuditInsertRepository
                    .saveIdamLogonAuditWithEncryption(idamLogonAudit, securityDbBackendEncryptionKey);
        } else {
            idamLogonAuditResponse = idamLogonAuditRepository.save(idamLogonAudit);
        }

        final String timestamp = timestampUtil.timestampConvertor(idamLogonAudit.getTimestamp());
        return new LogonLogPostResponse(new LogonLogResponse().toDto(idamLogonAuditResponse, timestamp));
    }

    private int calculateStartRecordNumber(final Page<IdamLogonAudit> logon) {
        return logon.getSize() * logon.getNumber() + 1;
    }

    private Pageable getPage(final String size, final String page) {
        final String pageSize = isEmpty(size) ? defaultPageSize : size.trim();
        final String pageNumber = isEmpty(page) ? "1" : page.trim();

        return of(parseInt(pageNumber) - 1, parseInt(pageSize), Sort.by("log_timestamp"));
    }

    public void deleteLogonLogById(final String logonId) {
        idamLogonAuditRepository.deleteById(Long.valueOf(logonId));
    }
}
