package uk.gov.hmcts.reform.laubackend.idam.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonInputParamsHolder;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogGetResponse;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;
import uk.gov.hmcts.reform.laubackend.idam.service.LogonLogService;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.EMAIL_ADDRESS;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.END_TIME;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.PAGE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.START_TIME;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.SIZE;
import static uk.gov.hmcts.reform.laubackend.idam.constants.LogonLogConstants.USER_ID;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifier.verifyRequestLogonLogParamsConditions;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifier.verifyRequestLogonParamsConditions;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyLogonLogRequestAreNotEmpty;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyRequestLogonParamsAreNotEmpty;

@RestController
@Slf4j
@Api(tags = "IdAM logon database operations.", value = "This is the Log and Audit "
        + "Back-End API that will audit IdAM logons. "
        + "The API will be invoked by IdAM service.")
@SuppressWarnings("PMD.ExcessiveImports")
public class IdamLogonAuditController {

    @Autowired
    private LogonLogService logonLogService;

    @ApiOperation(tags = "POST end-point", value = "Save IdAM logon audits", notes = "This operation will "
            + "persist IdAM logons entries which are posted in the request. Single IdAM LogonAudit per request will "
            + "be stored in the database.")
    @ApiResponses({
            @ApiResponse(code = 201,
                    message = "Created logonLog response - includes id from DB.",
                    response = LogonLogPostResponse.class),
            @ApiResponse(code = 400,
                    message = "Invalid logon audit",
                    response = LogonLogPostResponse.class),
            @ApiResponse(code = 403, message = "Forbidden",
                    response = LogonLogPostResponse.class),
            @ApiResponse(code = 500, message = "Internal Server Error",
                    response = LogonLogPostResponse.class)
    })
    @PostMapping(
            path = "/audit/logon",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<LogonLogPostResponse> saveLogonLog(@RequestBody final LogonLogPostRequest
                                                                     logonLogPostRequest) {
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
            return new ResponseEntity<>(null, BAD_REQUEST);
        } catch (final Exception exception) {
            log.error("saveLogonLog API call failed due to error - {}",
                    exception.getMessage(),
                    exception
            );
            return new ResponseEntity<>(null, INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(tags = "GET end-points", value = "Retrieve login audits", notes = "This operation will "
        + "query and return a list of logins based on the search conditions provided in the URL path.")
    @ApiResponses({
        @ApiResponse(code = 200,
            message = "Request executed successfully. Response contains of logoon logs",
            response = LogonLogGetResponse.class),
        @ApiResponse(code = 400,
            message =
                "Missing userId, emailAddress, startTimestamp or endTimestamp parameters.",
            response = LogonLogGetResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = LogonLogGetResponse.class)
    })
    @GetMapping(
        path = "/audit/logon",
        produces = APPLICATION_JSON_VALUE
    )
    @SuppressWarnings({"PMD.UseObjectForClearerAPI"})
    @ResponseBody
    public ResponseEntity<LogonLogGetResponse> getLogonLog(
        @ApiParam(value = "User ID", example = "3748238")
        @RequestParam(value = USER_ID, required = false) final String userId,
        @ApiParam(value = "Case Reference ID", example = "1615817621013640")
        @RequestParam(value = EMAIL_ADDRESS, required = false) final String emailAddress,
        @ApiParam(value = "Start Timestamp", example = "2021-06-23 22:20:05")
        @RequestParam(value = START_TIME, required = false) final String startTime,
        @ApiParam(value = "End Timestamp", example = "2021-08-23 22:20:05")
        @RequestParam(value = END_TIME, required = false) final String endTime,
        @ApiParam(value = "Size", example = "500")
        @RequestParam(value = SIZE, required = false) final String size,
        @ApiParam(value = "Page", example = "1")
        @RequestParam(value = PAGE, required = false) final String page) {

        try {
            final LogonInputParamsHolder inputParamsHolder = new LogonInputParamsHolder(userId,
                                                                                         emailAddress,
                                                                                         startTime,
                                                                                         endTime,
                                                                                         size,
                                                                                         page);
            verifyRequestLogonParamsAreNotEmpty(inputParamsHolder);
            verifyRequestLogonParamsConditions(inputParamsHolder);

            final LogonLogGetResponse logonLog = logonLogService.getLogonLog(inputParamsHolder);

            return new ResponseEntity<>(logonLog, OK);
        } catch (final InvalidRequestException invalidRequestException) {
            log.error(
                "getLogonLog API call failed due to error - {}",
                invalidRequestException.getMessage(),
                invalidRequestException
            );
            return new ResponseEntity<>(null, BAD_REQUEST);
        }
    }
}
