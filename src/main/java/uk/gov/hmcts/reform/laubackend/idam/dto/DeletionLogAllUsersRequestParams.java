package uk.gov.hmcts.reform.laubackend.idam.dto;

import org.springframework.web.bind.annotation.RequestParam;

public record DeletionLogAllUsersRequestParams(
    @RequestParam String page,
    @RequestParam String size,
    @RequestParam String sort) {
}
