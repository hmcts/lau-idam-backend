package uk.gov.hmcts.reform.laubackend.idam.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.laubackend.idam.service.UserDeletionAuditService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserDeletionAuditDeleteControllerTest {

    @Mock
    private UserDeletionAuditService userDeletionAuditService;

    @InjectMocks
    private UserDeletionAuditDeleteController controller;

    @Test
    void shouldReturnResponseEntityForDeleteUserDeletionRequest() {
        doNothing().when(userDeletionAuditService).deleteUserDeletionByUserId("1");
        ResponseEntity<Object> response = controller.deleteUserDeletionAudit("1");
        verify(userDeletionAuditService, times(1)).deleteUserDeletionByUserId("1");
        assertEquals(
            HttpStatus.NO_CONTENT,
            response.getStatusCode(),
            "Expected NO_CONTENT, but got " + response.getStatusCode());
    }

    @Test
    void shouldReturnBadRequest() {
        ResponseEntity<Object> response = controller.deleteUserDeletionAudit(null);
        assertEquals(
            HttpStatus.BAD_REQUEST,
            response.getStatusCode(),
            "Expected BAD_REQUEST, but got " + response.getStatusCode()
        );
    }

    @Test
    void shouldReturnNotFound() {
        doThrow(new EmptyResultDataAccessException(1))
            .when(userDeletionAuditService)
            .deleteUserDeletionByUserId("1");
        ResponseEntity<Object> response = controller.deleteUserDeletionAudit("1");
        verify(userDeletionAuditService, times(1)).deleteUserDeletionByUserId("1");
        assertEquals(
            HttpStatus.NOT_FOUND,
            response.getStatusCode(),
            "Expected NOT_FOUND, but got " + response.getStatusCode()
        );
    }

    @Test
    void shouldReturnInternalServerError() {
        doThrow(new RuntimeException())
            .when(userDeletionAuditService)
            .deleteUserDeletionByUserId("1");

        ResponseEntity<Object> response = controller.deleteUserDeletionAudit("1");
        verify(userDeletionAuditService, times(1)).deleteUserDeletionByUserId("1");
        assertEquals(
            HttpStatus.INTERNAL_SERVER_ERROR,
            response.getStatusCode(),
            "Expected INTERNAL_SERVER_ERROR, but got " + response.getStatusCode()
        );
    }
}
