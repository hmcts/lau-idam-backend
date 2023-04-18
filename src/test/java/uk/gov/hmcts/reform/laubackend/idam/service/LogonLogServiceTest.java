package uk.gov.hmcts.reform.laubackend.idam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.repository.IdamLogonAuditFindLogonRepository;
import uk.gov.hmcts.reform.laubackend.idam.repository.IdamLogonAuditRepository;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogGetResponse;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.sql.Timestamp.valueOf;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings({"PMD.UnusedPrivateField"})
class LogonLogServiceTest {

    @Mock
    private IdamLogonAuditRepository idamLogonAuditRepository;
    @Mock IdamLogonAuditFindLogonRepository idamLogonAuditFindLogonRepository;

    @Mock
    private TimestampUtil timestampUtil;

    @InjectMocks
    private LogonLogService logonLogService;

    @Test
    void shouldGetLogonRepositoryResponse() {
        final Timestamp timestamp = mock(Timestamp.class);
        final List<IdamLogonAudit> idamLogonAudits = Arrays.asList(getIdamLogonAuditEntity(timestamp));
        final Page<IdamLogonAudit> pageResults = new PageImpl<>(idamLogonAudits);

        setField(logonLogService, "defaultPageSize", "10000");

        final LogonInputParamsHolder inputParamsHolder = new LogonInputParamsHolder(
                "1",
                "2",
                null,
                null,
                null,
                null);

        when(idamLogonAuditFindLogonRepository
                .findIdamLogon("1", "2", null, null, null,
                        PageRequest.of(0, parseInt("10000"), Sort.by("log_timestamp"))))
                .thenReturn(pageResults);

        final LogonLogGetResponse logonLog = logonLogService.getLogonLog(inputParamsHolder);

        verify(idamLogonAuditFindLogonRepository, times(1))
                .findIdamLogon("1", "2", null, null, null,
                        PageRequest.of(0, parseInt("10000"), Sort.by("log_timestamp")));

        assertThat(logonLog.getLogonLog().size()).isEqualTo(1);
        assertThat(logonLog.getLogonLog().get(0).getUserId()).isEqualTo("1");
        assertThat(logonLog.getLogonLog().get(0).getEmailAddress()).isEqualTo("2");
        assertThat(logonLog.getLogonLog().get(0).getService()).isEqualTo("3");
        assertThat(logonLog.getLogonLog().get(0).getIpAddress()).isEqualTo("4");
        assertThat(logonLog.getLogonLog().get(0).getLoginState()).isEqualTo("5");
    }

    @Test
    void shouldPostLogonLog() {
        final Timestamp timestamp = valueOf(now());

        final IdamLogonAudit idamLogonAudit = new IdamLogonAudit();
        idamLogonAudit.setId(Long.valueOf(1));
        idamLogonAudit.setEmailAddress("some.random.mail@supercoolmail.com");
        idamLogonAudit.setIpAddress("random.ip.address");
        idamLogonAudit.setUserId("123");
        idamLogonAudit.setService("service");
        idamLogonAudit.setLoginState("AUTHENTICATE");
        idamLogonAudit.setTimestamp(timestamp);

        final LogonLog logonLog = new LogonLog("1",
                "2",
                "service",
                "4",
                "5",
                "6");

        when(idamLogonAuditRepository.save(any())).thenReturn(idamLogonAudit);

        final LogonLogPostResponse logonLogPostResponse = logonLogService.saveLogonLog(logonLog);

        verify(idamLogonAuditRepository, times(1)).save(any());
        assertThat(logonLogPostResponse.getLogonLog().getUserId()).isEqualTo("123");
        assertThat(logonLogPostResponse.getLogonLog().getService()).isEqualTo("service");
        assertThat(logonLogPostResponse.getLogonLog().getEmailAddress())
                .isEqualTo("some.random.mail@supercoolmail.com");
        assertThat(logonLogPostResponse.getLogonLog().getIpAddress()).isEqualTo("random.ip.address");
        assertThat(logonLogPostResponse.getLogonLog().getLoginState()).isEqualTo("AUTHENTICATE");
    }

    @Test
    void shouldDeleteIdamLogonId() {
        logonLogService.deleteLogonLogById("1");
        verify(idamLogonAuditRepository, times(1)).deleteById(Long.valueOf("1"));
    }

    private IdamLogonAudit getIdamLogonAuditEntity(final Timestamp timestamp) {
        final IdamLogonAudit idamLogonAudit = new IdamLogonAudit();
        idamLogonAudit.setUserId("1");
        idamLogonAudit.setEmailAddress("2");
        idamLogonAudit.setService("3");
        idamLogonAudit.setIpAddress("4");
        idamLogonAudit.setLoginState("5");
        idamLogonAudit.setTimestamp(timestamp);
        return idamLogonAudit;
    }
}
