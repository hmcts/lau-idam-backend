package uk.gov.hmcts.reform.laubackend.idam.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;
import uk.gov.hmcts.reform.laubackend.idam.request.LogonLogPostRequest;
import uk.gov.hmcts.reform.laubackend.idam.response.LogonLogPostResponse;
import uk.gov.hmcts.reform.laubackend.idam.service.LogonLogService;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifier.verifyRequestLogonLogParamsConditions;
import static uk.gov.hmcts.reform.laubackend.idam.utils.NotEmptyInputParamsVerifier.verifyLogonLogRequestAreNotEmpty;

@RestController
@Slf4j
@Api(tags = "IdAM logon database operations.", value = "This is the Log and Audit "
        + "Back-End API that will audit IdAM logons. "
        + "The API will be invoked by IdAM service.")
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
}
