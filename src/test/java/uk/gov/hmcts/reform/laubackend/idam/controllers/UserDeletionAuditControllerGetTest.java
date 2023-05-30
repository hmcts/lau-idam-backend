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
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogGetRequestParams;
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
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.GET_DELETED_ACCOUNTS_INVALID_REQUEST_EXCEPTION;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserDeletionAuditControllerGetTest {

    private static final String USERID = "1234";
    private static final String EMAIL = "john.smith@example.net";
    private static final String START_TIMESTAMP = "2023-05-23T07:34:12";

    @Mock
    private UserDeletionAuditService userDeletionAuditService;

    @Mock
    private AppInsights appInsights;

    @InjectMocks
    private UserDeletionAuditController controller;

    @Test
    void shouldReturnResponseForGetRequest() {
        final UserDeletionGetResponse response = mock(UserDeletionGetResponse.class);
        when(userDeletionAuditService.getUserDeletions(any())).thenReturn(response);
        DeletionLogGetRequestParams params = new DeletionLogGetRequestParams(
            USERID,
            EMAIL,
            null,
            null,
            START_TIMESTAMP,
            "2023-05-24T07:34:12",
            null,
            null
        );
        final ResponseEntity<UserDeletionGetResponse> responseEntity = controller.getUserDeletions(null, null, params);
        verify(userDeletionAuditService, times(1)).getUserDeletions(params);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Http status code mismatch");
    }

    @Test
    void shouldReturnBadRequestOnMissingGetTimestampArg() {
        DeletionLogGetRequestParams params = new DeletionLogGetRequestParams(
            USERID,
            EMAIL,
            null,
            null,
            START_TIMESTAMP,
            null,
            null,
            null
        );
        var response = controller.getUserDeletions(null, null, params);
        assertResponseIsBadRequest(response, "Http status code mismatch");
        verify(appInsights, times(1))
            .trackEvent(eq(GET_DELETED_ACCOUNTS_INVALID_REQUEST_EXCEPTION.toString()), anyMap());
    }

    @Test
    void shouldReturnBadRequestOnMissingRequiredArg() {
        DeletionLogGetRequestParams params = new DeletionLogGetRequestParams(
            "",
            "",
            null,
            null,
            START_TIMESTAMP,
            "2023-05-23T07:34:12",
            null,
            null
        );
        var response = controller.getUserDeletions(null, null, params);
        assertResponseIsBadRequest(response, "Http status code mismatch");
        verify(appInsights, times(1))
            .trackEvent(eq(GET_DELETED_ACCOUNTS_INVALID_REQUEST_EXCEPTION.toString()), anyMap());
    }

    void assertResponseIsBadRequest(ResponseEntity<UserDeletionGetResponse> response, String message) {
        HttpStatusCode statusCode = response.getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, statusCode, message);
    }

}
