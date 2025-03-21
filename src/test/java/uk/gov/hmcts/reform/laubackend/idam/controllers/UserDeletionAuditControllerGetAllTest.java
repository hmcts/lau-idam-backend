package uk.gov.hmcts.reform.laubackend.idam.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogAllUsersRequestParams;
import uk.gov.hmcts.reform.laubackend.idam.insights.AppInsights;
import uk.gov.hmcts.reform.laubackend.idam.response.UserDeletionGetResponse;
import uk.gov.hmcts.reform.laubackend.idam.service.UserDeletionAuditService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.GET_ALL_DELETED_ACCOUNTS_INVALID_REQUEST_EXCEPTION;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserDeletionAuditControllerGetAllTest {

    @Mock
    private UserDeletionAuditService userDeletionAuditService;

    @Mock
    private AppInsights appInsights;

    @InjectMocks
    private UserDeletionAuditController controller;

    @Test
    void shouldReturnResponseForGetAllUsersRequest() {
        final UserDeletionGetResponse response = mock(UserDeletionGetResponse.class);
        when(userDeletionAuditService.getAllDeletedUsers(any())).thenReturn(response);
        DeletionLogAllUsersRequestParams params = new DeletionLogAllUsersRequestParams(
            "1",
            "10",
            ""
        );
        final ResponseEntity<UserDeletionGetResponse> responseEntity = controller
            .getAllDeletedUsers(null, null, params);
        verify(userDeletionAuditService, times(1)).getAllDeletedUsers(params);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Http status code mismatch");
    }

    @Test
    void shouldReturnBadRequestOnMissingRequiredArg() {
        DeletionLogAllUsersRequestParams params = new DeletionLogAllUsersRequestParams(
            null,
            null,
            null
        );
        ResponseEntity<UserDeletionGetResponse> response = controller.getAllDeletedUsers(null, null, params);
        HttpStatusCode statusCode = response.getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, statusCode, "Http status code mismatch");
        verify(appInsights, times(1))
            .trackEvent(eq(GET_ALL_DELETED_ACCOUNTS_INVALID_REQUEST_EXCEPTION.toString()), anyMap());
    }

    @Test
    void shouldReturnBadRequestOnIncorrectSort() {
        DeletionLogAllUsersRequestParams params = new DeletionLogAllUsersRequestParams(
            "10",
            "10",
            "abc"
        );
        ResponseEntity<UserDeletionGetResponse> response = controller.getAllDeletedUsers(null, null, params);
        HttpStatusCode statusCode = response.getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, statusCode, "Http status code mismatch");
        verify(appInsights, times(1))
            .trackEvent(eq(GET_ALL_DELETED_ACCOUNTS_INVALID_REQUEST_EXCEPTION.toString()), anyMap());
    }
}
