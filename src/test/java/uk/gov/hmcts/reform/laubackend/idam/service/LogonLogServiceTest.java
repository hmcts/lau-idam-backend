package uk.gov.hmcts.reform.laubackend.idam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.repository.IdamLogonAuditRepository;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.sql.Timestamp;

import static java.sql.Timestamp.valueOf;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings({"PMD.UnusedPrivateField"})
class LogonLogServiceTest {

    @Mock
    private IdamLogonAuditRepository idamLogonAuditRepository;

    @Mock
    private TimestampUtil timestampUtil;

    @InjectMocks
    private LogonLogService logonLogService;

    @Test
    void shouldPostLogonLog() {
        final Timestamp timestamp = valueOf(now());

        final IdamLogonAudit idamLogonAudit = new IdamLogonAudit();
        idamLogonAudit.setId(Long.valueOf(1));
        idamLogonAudit.setEmailAddress("some.random.mail@supercoolmail.com");
        idamLogonAudit.setIpAddress("random.ip.address");
        idamLogonAudit.setUserId("123");
        idamLogonAudit.setService("service");
        idamLogonAudit.setTimestamp(timestamp);

        final LogonLog logonLog = new LogonLog("1",
                "2",
                "service",
                "4",
                "5");

        when(idamLogonAuditRepository.save(any())).thenReturn(idamLogonAudit);

        final LogonLogPostResponse logonLogPostResponse = logonLogService.saveLogonLog(logonLog);

        verify(idamLogonAuditRepository, times(1)).save(any());
        assertThat(logonLogPostResponse.getLogonLog().getUserId()).isEqualTo("123");
        assertThat(logonLogPostResponse.getLogonLog().getService()).isEqualTo("service");
        assertThat(logonLogPostResponse.getLogonLog().getEmailAddress())
                .isEqualTo("some.random.mail@supercoolmail.com");
        assertThat(logonLogPostResponse.getLogonLog().getIpAddress()).isEqualTo("random.ip.address");
    }
}