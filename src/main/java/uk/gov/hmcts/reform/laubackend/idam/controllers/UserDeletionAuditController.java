package uk.gov.hmcts.reform.laubackend.idam.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogAllUsersRequestParams;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogGetRequestParams;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;
import uk.gov.hmcts.reform.laubackend.idam.insights.AppInsights;
import uk.gov.hmcts.reform.laubackend.idam.request.UserDeletionPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.UserDeletionGetResponse;
import uk.gov.hmcts.reform.laubackend.idam.response.UserDeletionPostResponse;
import uk.gov.hmcts.reform.laubackend.idam.service.UserDeletionAuditService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.AUTHORISATION_HEADER;
import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.SERVICE_AUTHORISATION_HEADER;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.GET_ALL_DELETED_ACCOUNTS_INVALID_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.GET_DELETED_ACCOUNTS_INVALID_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.POST_DELETION_INVALID_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.POST_DELETION_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifier.verifyAllUserDeletionGetRequestParams;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifier.verifyUserDeletionGetRequestParams;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifier.verifyUserDeletionPostRequestParams;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyUserDeletionGetRequestParamsPresence;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyUserDeletionPostRequestParamsPresence;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User deletion operations", description = ""
    + "This is the Log and Audit Back-End API that will audit user account deletions. "
    + "The API will be invoked by IdAM service.")
@SuppressWarnings({"PMD.ExcessiveImports","PMD.UnnecessaryAnnotationValueElement", "PMD.ExceptionAsFlowControl"})
public class UserDeletionAuditController {

    private static final String EXCEPTION = "exception";

    private final AppInsights appInsights;
    private final UserDeletionAuditService userDeletionAuditService;

    @Operation(tags = "User Accounts endpoints", summary = "Save IdAM user deletion audit",
        description = "This operation will persist IdAM user deletion entries which are posted in the request. "
        + "Single IdAM UserDeletionAudit per request will be stored in the database.")
    @ApiResponse(
        responseCode = "201",
        description = "Created userDeletionAudit response - includes id from DB.",
        content = { @Content(schema = @Schema(implementation = UserDeletionPostResponse.class))})
    @ApiResponse(
        responseCode = "400",
        description = "Invalid userDeletion audit",
        content = { @Content(schema = @Schema(implementation = UserDeletionPostResponse.class))})
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = { @Content(schema = @Schema(implementation = UserDeletionPostResponse.class))})
    @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = { @Content(schema = @Schema(implementation = UserDeletionPostResponse.class))})
    @PostMapping(
        path = "/audit/deletedAccounts",
        produces = APPLICATION_JSON_VALUE,
        consumes = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDeletionPostResponse> saveUserDeletion(
        @Parameter(name = "Service Authorization", example = "Bearer eyJ0eXAiOiJK...")
        @RequestHeader(SERVICE_AUTHORISATION_HEADER) String serviceAuthToken,
        @RequestBody final UserDeletionPostRequest userDeletionPostRequest
    ) {
        try {
            if (userDeletionPostRequest == null) {
                throw new InvalidRequestException("Request body is required", HttpStatus.BAD_REQUEST);
            }

            List<DeletionLog> requestLogs = userDeletionPostRequest.getDeletionLogs();
            verifyUserDeletionPostRequestParamsPresence(requestLogs);
            verifyUserDeletionPostRequestParams(requestLogs);
            List<DeletionLog> deletionLogs = userDeletionAuditService.saveUserDeletion(requestLogs);
            final UserDeletionPostResponse userDeletionPostResponse = new UserDeletionPostResponse(deletionLogs);

            return new ResponseEntity<>(userDeletionPostResponse, HttpStatus.CREATED);
        } catch (final InvalidRequestException ire) {
            log.error("saveUserDeletion API call failed due to error - {}",
                      ire.getMessage(),
                      ire);
            appInsights.trackEvent(
                POST_DELETION_INVALID_REQUEST_EXCEPTION.toString(),
                appInsights.trackingMap(EXCEPTION, ire.getMessage())
            );
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        } catch (final Exception exception) {
            log.error("saveUserDeletion API call failed due to error - {}",
                      exception.getMessage(),
                      exception);
            appInsights.trackEvent(
                POST_DELETION_REQUEST_EXCEPTION.toString(),
                appInsights.trackingMap(EXCEPTION, exception.getMessage())
            );
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        tags = "User Accounts endpoints",
        summary = "Retrieve deleted user accounts",
        description = "Query deleted user accounts based on search conditions provided via URL params"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Request executed successfully. Response will contain list of deletion logs",
        content = { @Content(schema = @Schema(implementation = UserDeletionGetResponse.class))})
    @ApiResponse(
        responseCode = "400",
        description = "Missing required parameter(s). startTimestamp and endStamp are both "
            + "required and at least one of userId, emailAddress, firstName or lastName",
        content = { @Content(schema = @Schema(implementation = UserDeletionGetResponse.class))})
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = { @Content(schema = @Schema(implementation = UserDeletionGetResponse.class))})
    @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = { @Content(schema = @Schema(implementation = UserDeletionGetResponse.class))})
    @GetMapping(
        path = "/audit/deletedAccounts",
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDeletionGetResponse> getUserDeletions(
        @Parameter(name = "Authorization", example = "Bearer eyJ0eXAiOiJK.........")
        @RequestHeader(value = AUTHORISATION_HEADER) String authToken,
        @Parameter(name = "Service Authorization", example = "Bearer eyJ0eXAiOiJK.........")
        @RequestHeader(value = SERVICE_AUTHORISATION_HEADER) String serviceAuthToken,
        DeletionLogGetRequestParams requestParams
    ) {
        try {
            verifyUserDeletionGetRequestParamsPresence(requestParams);
            verifyUserDeletionGetRequestParams(requestParams);

            final UserDeletionGetResponse response = userDeletionAuditService.getUserDeletions(requestParams);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (final InvalidRequestException ire) {
            log.error("getDeletedAccounts API call failed due to error - {}",
                      ire.getMessage(),
                      ire
            );
            appInsights.trackEvent(
                GET_DELETED_ACCOUNTS_INVALID_REQUEST_EXCEPTION.toString(),
                appInsights.trackingMap(EXCEPTION, ire.getMessage())
            );
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(
        tags = "User Accounts endpoints",
        summary = "Retrieve all deleted user accounts",
        description = "Query all deleted user accounts based on size and sort"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Request executed successfully. Response will contain list of deleted users",
        content = { @Content(schema = @Schema(implementation = UserDeletionGetResponse.class))})
    @ApiResponse(
        responseCode = "400",
        description = "Missing required parameter(s). size is required ",
        content = { @Content(schema = @Schema(implementation = UserDeletionGetResponse.class))})
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = { @Content(schema = @Schema(implementation = UserDeletionGetResponse.class))})
    @ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = { @Content(schema = @Schema(implementation = UserDeletionGetResponse.class))})
    @GetMapping(
        path = "/audit/getAllDeletedAccounts",
        produces = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDeletionGetResponse> getAllDeletedUsers(
        @Parameter(name = "Authorization", example = "Bearer eyJ0eXYiOiJK.........")
        @RequestHeader(value = AUTHORISATION_HEADER) String authToken,
        @Parameter(name = "Service Authorization", example = "Bearer eyJ0eXYiOiJK.........")
        @RequestHeader(value = SERVICE_AUTHORISATION_HEADER) String serviceAuthToken,
        DeletionLogAllUsersRequestParams requestParams
    ) {
        try {
            verifyAllUserDeletionGetRequestParams(requestParams);

            final UserDeletionGetResponse response = userDeletionAuditService.getAllDeletedUsers(requestParams);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (final InvalidRequestException ire) {
            log.error("getAllDeletedAccounts API call failed due to error - {}",
                      ire.getMessage(),
                      ire
            );
            appInsights.trackEvent(
                GET_ALL_DELETED_ACCOUNTS_INVALID_REQUEST_EXCEPTION.toString(),
                appInsights.trackingMap(EXCEPTION, ire.getMessage())
            );
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
