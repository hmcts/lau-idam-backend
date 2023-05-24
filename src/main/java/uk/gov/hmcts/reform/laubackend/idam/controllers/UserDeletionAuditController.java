package uk.gov.hmcts.reform.laubackend.idam.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;
import uk.gov.hmcts.reform.laubackend.idam.insights.AppInsights;
import uk.gov.hmcts.reform.laubackend.idam.request.UserDeletionPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.UserDeletionPostResponse;
import uk.gov.hmcts.reform.laubackend.idam.service.UserDeletionAuditService;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.SERVICE_AUTHORISATION_HEADER;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.POST_DELETION_INVALID_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.POST_DELETION_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifier.verifyUserDeletionRequestParams;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyUserDeletionPostRequestParamsPresence;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User deletion operations", description = ""
    + "This is the Log and Audit Back-End API that will audit user account deletions. "
    + "The API will be invoked by IdAM service.")
public class UserDeletionAuditController {

    private final AppInsights appInsights;
    private final UserDeletionAuditService userDeletionAuditService;

    @Operation(tags = "POST end-point", summary = "Save IdAM user deletion audit", description = "This operation will "
        + "persist IdAM user deletion entries which are posted in the request. Single IdAM UserDeletionAudit "
        + "per request will be stored in the database.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Created userDeletionAudit response - includes id from DB.",
            content = { @Content(schema = @Schema(implementation = UserDeletionPostResponse.class))}),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid userDeletion audit",
            content = { @Content(schema = @Schema(implementation = UserDeletionPostResponse.class))}),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = { @Content(schema = @Schema(implementation = UserDeletionPostResponse.class))}),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = { @Content(schema = @Schema(implementation = UserDeletionPostResponse.class))})
    })
    @PostMapping(
        path = "/audit/deletedAccounts",
        produces = APPLICATION_JSON_VALUE,
        consumes = APPLICATION_JSON_VALUE
    )
    @ResponseBody
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
            verifyUserDeletionRequestParams(requestLogs);
            List<DeletionLog> deletionLogs = userDeletionAuditService.saveUserDeletion(requestLogs);
            final UserDeletionPostResponse userDeletionPostResponse = new UserDeletionPostResponse(deletionLogs);

            return new ResponseEntity<>(userDeletionPostResponse, HttpStatus.CREATED);
        } catch (final InvalidRequestException ire) {
            log.error("saveUserDeletion API call failed due to error - {}",
                      ire.getMessage(),
                      ire);
            appInsights.trackEvent(
                POST_DELETION_INVALID_REQUEST_EXCEPTION.toString(),
                appInsights.trackingMap("exception", ire.getMessage())
            );
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        } catch (final Exception exception) {
            log.error("saveUserDeletion API call failed due to error - {}",
                      exception.getMessage(),
                      exception);
            appInsights.trackEvent(
                POST_DELETION_REQUEST_EXCEPTION.toString(),
                appInsights.trackingMap("exception", exception.getMessage())
            );
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
