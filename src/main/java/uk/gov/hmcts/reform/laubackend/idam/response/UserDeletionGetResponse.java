package uk.gov.hmcts.reform.laubackend.idam.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;

import java.util.List;

@Getter
@Setter
@Builder
@Schema(description = "IdAM user deletion audit GET Response")
public class UserDeletionGetResponse {
    private List<DeletionLog> deletionLogs;

    @Schema(description = "The index of the first record out of the full result set provided in the result set")
    private Long startRecordNumber;

    @Schema(description = "Indicates whether there are more records beyond the current page in the full result set")
    private Boolean moreRecords;

    @Schema(description = "The total number of records in the full result set")
    private long totalNumberOfRecords;
}
