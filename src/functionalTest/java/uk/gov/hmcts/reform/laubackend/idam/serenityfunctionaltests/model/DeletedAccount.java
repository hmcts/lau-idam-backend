package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeletedAccount {
    private String userId;
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String deletionTimestamp;
}
