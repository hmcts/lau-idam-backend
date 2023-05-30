package uk.gov.hmcts.reform.laubackend.idam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.RequestParam;

@Schema(description = "Data model for the IdAM user deletion request")
public record DeletionLogGetRequestParams(
    @RequestParam String userId,
    @RequestParam String emailAddress,
    @RequestParam String firstName,
    @RequestParam String lastName,
    @RequestParam String startTimestamp,
    @RequestParam String endTimestamp,
    @RequestParam String size,
    @RequestParam String page
) {
}





