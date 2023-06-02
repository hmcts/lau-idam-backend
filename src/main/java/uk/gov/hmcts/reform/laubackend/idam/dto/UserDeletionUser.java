package uk.gov.hmcts.reform.laubackend.idam.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.laubackend.idam.domain.UserDeletionAudit;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Data model for the IdAM user deletion request")
public class UserDeletionUser {

    @Schema(description = "The user who has been deleted")
    String userId;
    @Schema(description = "The email address of deleted user")
    String emailAddress;
    @Schema(description = "First name of deleted user")
    String firstName;
    @Schema(description = "Last name of deleted user")
    String lastName;

    public static UserDeletionUser toDto(final UserDeletionAudit userDeletionAudit) {
        return new UserDeletionUser(userDeletionAudit.getUserId(),
                                    userDeletionAudit.getEmailAddress(),
                                    userDeletionAudit.getFirstName(),
                                    userDeletionAudit.getLastName());
    }
}
