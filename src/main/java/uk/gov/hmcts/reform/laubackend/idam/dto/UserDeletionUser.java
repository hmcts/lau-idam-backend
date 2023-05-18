package uk.gov.hmcts.reform.laubackend.idam.dto;



public record UserDeletionUser(
    String userId,
    String emailAddress,
    String firstName,
    String lastName
) { }
