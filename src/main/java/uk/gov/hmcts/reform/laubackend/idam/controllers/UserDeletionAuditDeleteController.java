package uk.gov.hmcts.reform.laubackend.idam.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidRequestException;
import uk.gov.hmcts.reform.laubackend.idam.service.UserDeletionAuditService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.hmcts.reform.laubackend.idam.utils.InputParamsVerifierHelper.verifyIdNotEmpty;

@Hidden
@RestController
@Slf4j
@Tag(
    name = "IdAM user deletion audit entries DELETE operation",
    description = "Deletion endpoint to facilitate cleanup after functional and e2e tests"
)
@RequiredArgsConstructor
public class UserDeletionAuditDeleteController {

    private final UserDeletionAuditService service;

    public static final String DELETE_ENDPOINT = "/audit/idamUser/deleteIdamUserRecord";

    @Operation(
        tags = "DELETE end-point",
        summary = "Delete IdAM user deletion audit record from the database",
        description = "This API will delete a record from the lau-idam database for the given entry id. "
        + "It is only intended to be called from the test API for testing purposes"
    )
    @ApiResponse(responseCode = "204", description = "UserDeletionAudit record has been deleted")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "IdAM user deletion audit id not found in the database")
    @ApiResponse(responseCode = "400", description = "Missing IdAM user deletion audit id from the API request")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    @DeleteMapping(
        path = UserDeletionAuditDeleteController.DELETE_ENDPOINT,
        produces = APPLICATION_JSON_VALUE,
        consumes = APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> deleteUserDeletionAudit(@RequestParam("userId") String userId) {
        try {
            verifyIdNotEmpty(userId);
            service.verifyUserIdExists(userId);
            service.deleteUserDeletionByUserId(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (InvalidRequestException ire) {
            return getExceptionResponseEntity(ire, HttpStatus.BAD_REQUEST);
        } catch (EmptyResultDataAccessException erdae) {
            return getExceptionResponseEntity(erdae, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return getExceptionResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Object> getExceptionResponseEntity(final Exception exception, final HttpStatus httpStatus) {
        log.error(
            "deleteLogonLog API call failed due to error - {}",
            exception.getMessage(),
            exception
        );
        return new ResponseEntity<>(httpStatus);
    }
}
