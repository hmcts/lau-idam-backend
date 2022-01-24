package uk.gov.hmcts.reform.laubackend.idam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.repository.IdamLogonAuditRepository;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogGetResponse;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogResponse;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Integer.parseInt;
import static org.springframework.data.domain.PageRequest.of;
import static uk.gov.hmcts.reform.laubackend.idam.response.LogonLogGetResponse.logonLogResponse;

@Service
public class LogonLogService {

    @Autowired
    private IdamLogonAuditRepository idamLogonAuditRepository;

    @Autowired
    private TimestampUtil timestampUtil;

    public LogonLogGetResponse getLogonLog(final LogonInputParamsHolder inputParamsHolder) {

        final Page<IdamLogonAudit> logonLog = idamLogonAuditRepository.findIdamLogon(
                inputParamsHolder.getUserId(),
                inputParamsHolder.getEmailAddress(),
                timestampUtil.getTimestampValue(inputParamsHolder.getStartTime()),
                timestampUtil.getTimestampValue(inputParamsHolder.getEndTime()),
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
        idamLogonAudit.setUserId(logonLog.getUserId());
        idamLogonAudit.setService(logonLog.getService());
        idamLogonAudit.setIpAddress(logonLog.getIpAddress());
        idamLogonAudit.setEmailAddress(logonLog.getEmailAddress());
        idamLogonAudit.setTimestamp(timestampUtil.getUtcTimestampValue(logonLog.getTimestamp()));

        final IdamLogonAudit idamLogonAuditResponse = idamLogonAuditRepository.save(idamLogonAudit);
        final String timestamp = timestampUtil.timestampConvertor(idamLogonAudit.getTimestamp());

        return new LogonLogPostResponse(new LogonLogResponse().toDto(idamLogonAuditResponse, timestamp));
    }

    private int calculateStartRecordNumber(final Page<IdamLogonAudit> logon) {
        return logon.getSize() * logon.getNumber() + 1;
    }

    private Pageable getPage(final String size, final String page) {
        final String pageSize = Optional.ofNullable(size).orElse("10000");
        final String pageNumber = Optional.ofNullable(page).orElse("1");

        return of(parseInt(pageNumber) - 1, parseInt(pageSize), Sort.by("timestamp"));
    }

    public void deleteLogonLogById(final String logonId) {
        idamLogonAuditRepository.deleteById(Long.valueOf(logonId));
    }
}
