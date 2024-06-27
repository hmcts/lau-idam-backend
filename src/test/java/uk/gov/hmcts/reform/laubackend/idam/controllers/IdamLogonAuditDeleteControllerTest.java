package uk.gov.hmcts.reform.laubackend.idam.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.laubackend.idam.service.LogonLogService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class IdamLogonAuditDeleteControllerTest {

    @Mock
    private LogonLogService logonLogService;

    @InjectMocks
    private IdamLogonAuditDeleteController idamLogonAuditDeleteController;

    @Test
    void shouldReturnResponseEntityForDeleteLogonLogRequest() {

        doNothing().when(logonLogService).deleteLogonLogById("1");

        final ResponseEntity<Object> responseEntity = idamLogonAuditDeleteController.deleteLogonLog("1");

        verify(logonLogService, times(1)).deleteLogonLogById("1");
        assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
    }

    @Test
    void shouldReturnBadRequestResponseEntityForDeleteLogonLogRequest() {

        final ResponseEntity<Object> responseEntity = idamLogonAuditDeleteController.deleteLogonLog(null);

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void shouldReturnNotFoundResponseEntityForDeleteLogonLogRequest() {
        doThrow(new EmptyResultDataAccessException(1))
                .when(logonLogService)
                .deleteLogonLogById("1");

        final ResponseEntity<Object> responseEntity = idamLogonAuditDeleteController.deleteLogonLog("1");

        verify(logonLogService, times(1)).deleteLogonLogById("1");
        assertThat(responseEntity.getStatusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    void shouldReturnInternalServerErrorResponseEntityForDeleteLogonLoghRequest() {
        doThrow(new RuntimeException())
                .when(logonLogService)
                .deleteLogonLogById("1");

        final ResponseEntity<Object> responseEntity = idamLogonAuditDeleteController.deleteLogonLog("1");

        verify(logonLogService, times(1)).deleteLogonLogById("1");
        assertThat(responseEntity.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }
}
