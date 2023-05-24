package uk.gov.hmcts.reform.laubackend.idam.controllers;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;
import uk.gov.hmcts.reform.laubackend.idam.insights.AppInsights;
import uk.gov.hmcts.reform.laubackend.idam.request.UserDeletionPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.UserDeletionPostResponse;
import uk.gov.hmcts.reform.laubackend.idam.service.UserDeletionAuditService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.POST_DELETION_INVALID_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.POST_DELETION_REQUEST_EXCEPTION;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserDeletionAuditControllerTest {

    private static final String USERID = "1234";
    private static final String EMAIL = "john.smith@example.net";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Smith";
    private static final String TIMESTAMP = "2023-05-23T11:29:05.023Z";

    @Mock
    private UserDeletionAuditService userDeletionAuditService;

    @Mock
    private AppInsights appInsights;

    @InjectMocks
    private UserDeletionAuditController controller;

    @Test
    void shouldReturnCreatedResponseForPostRequest() {
        when(userDeletionAuditService.saveUserDeletion(any())).thenReturn(null);

        final DeletionLog deletionLog = new DeletionLog(USERID, EMAIL, FIRST_NAME, LAST_NAME, TIMESTAMP);
        final List<DeletionLog> deletionLogs = Arrays.asList(deletionLog);
        final UserDeletionPostRequest request = new UserDeletionPostRequest(deletionLogs);

        final var responseEntity = controller.saveUserDeletion(null, request);
        verify(userDeletionAuditService, times(1)).saveUserDeletion(deletionLogs);
        verifyNoInteractions(appInsights);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode(), "Response code not what was expected");
    }

    @Test
    void shouldReturnBadRequestOnMissingArgs() {

        final var deletionLog1 = new DeletionLog(USERID, EMAIL, FIRST_NAME, LAST_NAME, TIMESTAMP);
        final var deletionLog2 = new DeletionLog(USERID, null, FIRST_NAME, LAST_NAME, TIMESTAMP);
        final var deletionLog3 = new DeletionLog(USERID, EMAIL, FIRST_NAME, LAST_NAME, TIMESTAMP);
        final var deletionLogs = Arrays.asList(deletionLog1, deletionLog2, deletionLog3);
        final UserDeletionPostRequest request = new UserDeletionPostRequest(deletionLogs);

        final var responseEntity = controller.saveUserDeletion(null, request);
        verify(appInsights, times(1))
            .trackEvent(eq(POST_DELETION_INVALID_REQUEST_EXCEPTION.toString()), anyMap());
        assertResponseIsBadRequest(responseEntity, "Response is not what was expected");
    }

    @Test
    void shouldReturnInternalServerErrorOnError() {
        final DeletionLog deletionLog = new DeletionLog(USERID, EMAIL, FIRST_NAME, LAST_NAME, TIMESTAMP);
        final var deletionLogs = Arrays.asList(deletionLog);
        final UserDeletionPostRequest request = new UserDeletionPostRequest(deletionLogs);

        when(userDeletionAuditService.saveUserDeletion(any())).thenThrow(NullPointerException.class);

        final var responseEntity = controller.saveUserDeletion(null, request);
        verify(appInsights, times(1))
            .trackEvent(eq(POST_DELETION_REQUEST_EXCEPTION.toString()), anyMap());
        assertEquals(
            HttpStatus.INTERNAL_SERVER_ERROR,
            responseEntity.getStatusCode(),
            "Response is not what was expected"
        );
    }

    @Test
    void shouldReturnBadRequestOnEmptyRequest() {
        final var responseEntity = controller.saveUserDeletion(null, null);
        assertResponseIsBadRequest(responseEntity, "Empty request didn't return BAD_REQUEST");
    }

    @Test
    void shouldReturnBadRequestOnInvalidParams() {
        final String assertMessage = "Invalid request param didn't return BAD_REQUEST";

        String longUserId = StringUtils.repeat("a", 65);
        var log = new DeletionLog(longUserId, EMAIL, FIRST_NAME, LAST_NAME, TIMESTAMP);
        UserDeletionPostRequest request = new UserDeletionPostRequest(Arrays.asList(log));
        var response = controller.saveUserDeletion(null, request);
        assertResponseIsBadRequest(response, assertMessage);

        String longEmail = StringUtils.repeat("a", 40) + "@" + StringUtils.repeat("b", 40);
        log = new DeletionLog(USERID, longEmail, FIRST_NAME, LAST_NAME, TIMESTAMP);
        request = new UserDeletionPostRequest(Arrays.asList(log));
        response = controller.saveUserDeletion(null, request);
        assertResponseIsBadRequest(response, assertMessage);

        String longFirstName = StringUtils.repeat("a", 65);
        log = new DeletionLog(USERID, EMAIL, longFirstName, LAST_NAME, TIMESTAMP);
        request = new UserDeletionPostRequest(Arrays.asList(log));
        response = controller.saveUserDeletion(null, request);
        assertResponseIsBadRequest(response, assertMessage);

        String longLastName = StringUtils.repeat("a", 65);
        log = new DeletionLog(USERID, EMAIL, FIRST_NAME, longLastName, TIMESTAMP);
        request = new UserDeletionPostRequest(Arrays.asList(log));
        response = controller.saveUserDeletion(null, request);
        assertResponseIsBadRequest(response, assertMessage);

        String invalidTimestamp = "1";
        log = new DeletionLog(USERID, EMAIL, FIRST_NAME, LAST_NAME, invalidTimestamp);
        request = new UserDeletionPostRequest(Arrays.asList(log));
        response = controller.saveUserDeletion(null, request);
        assertResponseIsBadRequest(response, assertMessage);

    }

    void assertResponseIsBadRequest(ResponseEntity<UserDeletionPostResponse> response, String message) {
        HttpStatusCode statusCode = response.getStatusCode();
        assertEquals(HttpStatus.BAD_REQUEST, statusCode, message);
    }

}
