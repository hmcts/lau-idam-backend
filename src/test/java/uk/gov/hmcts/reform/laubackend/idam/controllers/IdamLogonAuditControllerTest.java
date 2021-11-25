package uk.gov.hmcts.reform.laubackend.idam.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;
import uk.gov.hmcts.reform.laubackend.idam.service.LogonLogService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IdamLogonAuditControllerTest {

    @Mock
    private LogonLogService logonLogService;

    @InjectMocks
    private IdamLogonAuditController idamLogonAuditController;

    @Test
    void shouldReturnResponseEntityForPostRequest() {
        final LogonLogPostResponse logonLogPostResponse = mock(LogonLogPostResponse.class);

        when(logonLogService.saveLogonLog(any())).thenReturn(
                logonLogPostResponse);

        final LogonLog logonLog = new LogonLog("1",
                "frank.lampard@chelsea.com",
                "holla-service",
                "3.3.4.65.7",
                "2021-08-23T22:20:05.023Z");


        final LogonLogPostRequest logonLogPostRequest = new LogonLogPostRequest();
        logonLogPostRequest.setLogonLog(logonLog);

        final ResponseEntity<LogonLogPostResponse> responseEntity = idamLogonAuditController.saveLogonLog(
                logonLogPostRequest
        );

        verify(logonLogService, times(1)).saveLogonLog(logonLog);
        assertThat(responseEntity.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    void shouldReturnBadRequestResponseEntityForPostRequest() {
        final LogonLog logonLog = new LogonLog("1",
                "john.terry@chelsea.com",
                null,
                "3.3.4.65.7",
                "2021-08-23T22:20:05.023Z");


        final LogonLogPostRequest logonLogPostRequest = new LogonLogPostRequest();
        logonLogPostRequest.setLogonLog(logonLog);
        final ResponseEntity<LogonLogPostResponse> responseEntity = idamLogonAuditController.saveLogonLog(
                logonLogPostRequest
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void shouldReturnInternalServerErrorForPostRequest() {
        final LogonLog logonLog = new LogonLog("1",
                "frank.lampard@chelsea.com",
                "holla-service",
                "3.3.4.65.7",
                "2021-08-23T22:20:05.023Z");


        final LogonLogPostRequest logonLogPostRequest = new LogonLogPostRequest();
        logonLogPostRequest.setLogonLog(logonLog);

        given(logonLogService.saveLogonLog(any()))
                .willAnswer(invocation -> new Exception("Some terrible exception happened"));

        final ResponseEntity<LogonLogPostResponse> responseEntity = idamLogonAuditController.saveLogonLog(
                logonLogPostRequest
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }
}