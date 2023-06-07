package uk.gov.hmcts.reform.laubackend.idam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.laubackend.idam.domain.UserDeletionAudit;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Data model for the IdAM user deletion request")
public class DeletionLog implements Serializable {

    public static final long serialVersionUID = 432973389L;

    @Schema(description = "IdAM ID of the deleted user.")
    String userId;
    @Schema(description = "Email address/username of the deleted user.")
    String emailAddress;
    @Schema(description = "First name of deleted user")
    String firstName;
    @Schema(description = "Last name of deleted user")
    String lastName;
    @Schema(description = "When the operation took place with microseconds in iso-8601-date-and-time-format.")
    String deletionTimestamp;

    public static DeletionLog toDto(final UserDeletionAudit userDeletionAudit) {
        final TimestampUtil timestampUtil = new TimestampUtil();
        final String timestamp = timestampUtil.timestampConvertor(userDeletionAudit.getTimestamp());
        return new DeletionLog(userDeletionAudit.getUserId(),
                               userDeletionAudit.getEmailAddress(),
                               userDeletionAudit.getFirstName(),
                               userDeletionAudit.getLastName(),
                               timestamp);
    }
}
