package uk.gov.hmcts.reform.laubackend.idam.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;
import uk.gov.hmcts.reform.laubackend.idam.insights.AppInsights;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogGetResponse;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;
import uk.gov.hmcts.reform.laubackend.idam.service.LogonLogService;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.AUTHORISATION_HEADER;
import static uk.gov.hmcts.reform.laubackend.idam.constants.CommonConstants.SERVICE_AUTHORISATION_HEADER;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.EMAIL_ADDRESS;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.END_TIME;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.PAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.PERF_THRESHOLD_MESSAGE_ABOVE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.PERF_THRESHOLD_MESSAGE_BELOW;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.PERF_TOLERANCE_THRESHOLD_MS;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.SIZE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.START_TIME;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.USER_ID;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.GET_LOGON_REQUEST_INFO;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.GET_LOGON_REQUEST_INVALID_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.POST_LOGON_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.POST_LOGON_REQUEST_INVALID_REQUEST_EXCEPTION;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifier.verifyRequestLogonLogParamsConditions;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifier.verifyRequestLogonParamsConditions;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyLogonLogRequestAreNotEmpty;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyRequestLogonParamsAreNotEmpty;

@RestController
@Slf4j
@Tag(name = "IdAM logon database operations.", description = "This is the Log and Audit "
        + "Back-End API that will audit IdAM logons. "
        + "The API will be invoked by IdAM service.")
@SuppressWarnings({"PMD.ExcessiveImports","PMD.UnnecessaryAnnotationValueElement"})
public class IdamLogonAuditController {

    @Autowired
    private LogonLogService logonLogService;

    @Autowired
    private AppInsights appInsights;

    @Operation(tags = "POST end-point", summary = "Save IdAM logon audits", description = "This operation will "
            + "persist IdAM logons entries which are posted in the request. Single IdAM LogonAudit per request will "
            + "be stored in the database.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Created logonLog response - includes id from DB.",
            content = { @Content(schema = @Schema(implementation = LogonLogPostResponse.class))}),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid logon audit",
            content = { @Content(schema = @Schema(implementation = LogonLogPostResponse.class))}),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = { @Content(schema = @Schema(implementation = LogonLogPostResponse.class))}),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = { @Content(schema = @Schema(implementation = LogonLogPostResponse.class))})
    })
    @PostMapping(
            path = "/audit/logon",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<LogonLogPostResponse> saveLogonLog(
        @Parameter(name = "Service Authorization", example = "Bearer eyJ0eXAiOiJK.........")
        @RequestHeader(value = SERVICE_AUTHORISATION_HEADER) String serviceAuthToken,
        @RequestBody final LogonLogPostRequest logonLogPostRequest) {
        try {
            verifyLogonLogRequestAreNotEmpty(logonLogPostRequest.getLogonLog());
            verifyRequestLogonLogParamsConditions(logonLogPostRequest.getLogonLog());

            final LogonLogPostResponse logonLogPostResponse = logonLogService
                    .saveLogonLog(logonLogPostRequest.getLogonLog());

            return new ResponseEntity<>(logonLogPostResponse, CREATED);
        } catch (final InvalidRequestException invalidRequestException) {
            log.error("saveLogonLog API call failed due to error - {}",
                    invalidRequestException.getMessage(),
                    invalidRequestException
            );
            appInsights.trackEvent(POST_LOGON_REQUEST_INVALID_REQUEST_EXCEPTION.toString(), appInsights.trackingMap(
                "exception", invalidRequestException.getMessage()));
            return new ResponseEntity<>(null, BAD_REQUEST);
        } catch (final Exception exception) {
            log.error("saveLogonLog API call failed due to error - {}",
                    exception.getMessage(),
                    exception
            );
            appInsights.trackEvent(POST_LOGON_REQUEST_EXCEPTION.toString(), appInsights.trackingMap(
                "exception", exception.getMessage()));
            return new ResponseEntity<>(null, INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(tags = "GET end-points", summary = "Retrieve login audits", description = "This operation will "
        + "query and return a list of logins based on the search conditions provided in the URL path.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Request executed successfully. Response contains of logon logs",
            content = { @Content(schema = @Schema(implementation = LogonLogGetResponse.class))}),
        @ApiResponse(
            responseCode = "400",
            description = "Missing userId, emailAddress, startTimestamp or endTimestamp parameters.",
            content = { @Content(schema = @Schema(implementation = LogonLogGetResponse.class))}),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = { @Content(schema = @Schema(implementation = LogonLogGetResponse.class))}),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = { @Content(schema = @Schema(implementation = LogonLogGetResponse.class))}),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = { @Content(schema = @Schema(implementation = LogonLogGetResponse.class))})
    })
    @GetMapping(
        path = "/audit/logon",
        produces = APPLICATION_JSON_VALUE
    )
    @SuppressWarnings({"PMD.UseObjectForClearerAPI"})
    @ResponseBody
    public ResponseEntity<LogonLogGetResponse> getLogonLog(
        @Parameter(name = "Authorization", example = "Bearer eyJ0eXAiOiJK.........")
        @RequestHeader(value = AUTHORISATION_HEADER) String authToken,
        @Parameter(name = "Service Authorization", example = "Bearer eyJ0eXAiOiJK.........")
        @RequestHeader(value = SERVICE_AUTHORISATION_HEADER) String serviceAuthToken,
        @Parameter(name = "User ID", example = "3748238")
        @RequestParam(value = USER_ID, required = false) final String userId,
        @Parameter(name = "Email Address", example = "firstname.lastname@company.com")
        @RequestParam(value = EMAIL_ADDRESS, required = false) final String emailAddress,
        @Parameter(name = "Start Timestamp", example = "2021-06-23 22:20:05")
        @RequestParam(value = START_TIME, required = false) final String startTime,
        @Parameter(name = "End Timestamp", example = "2021-08-23 22:20:05")
        @RequestParam(value = END_TIME, required = false) final String endTime,
        @Parameter(name = "Size", example = "500")
        @RequestParam(value = SIZE, required = false) final String size,
        @Parameter(name = "Page", example = "1")
        @RequestParam(value = PAGE, required = false) final String page) {

        try {
            final LogonInputParamsHolder inputParamsHolder = new LogonInputParamsHolder(userId,
                                                                                         emailAddress,
                                                                                         startTime,
                                                                                         endTime,
                                                                                         size,
                                                                                         page);
            final long timeStart = System.currentTimeMillis();
            verifyRequestLogonParamsAreNotEmpty(inputParamsHolder);
            verifyRequestLogonParamsConditions(inputParamsHolder);

            final LogonLogGetResponse logonLog = logonLogService.getLogonLog(inputParamsHolder);
            final long timeEnd = System.currentTimeMillis();
            final String report = (timeEnd - timeStart) > PERF_TOLERANCE_THRESHOLD_MS
                ? PERF_THRESHOLD_MESSAGE_ABOVE : PERF_THRESHOLD_MESSAGE_BELOW;
            appInsights.trackEvent(GET_LOGON_REQUEST_INFO.toString(), appInsights.trackingMap(
                "GET /audit/logon", report));
            return new ResponseEntity<>(logonLog, OK);
        } catch (final InvalidRequestException invalidRequestException) {
            log.error(
                "getLogonLog API call failed due to error - {}",
                invalidRequestException.getMessage(),
                invalidRequestException
            );
            appInsights.trackEvent(GET_LOGON_REQUEST_INVALID_REQUEST_EXCEPTION.toString(), appInsights.trackingMap(
                "exception", invalidRequestException.getMessage()));
            return new ResponseEntity<>(null, BAD_REQUEST);
        }
    }
}
