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
    private Long startRecordNumber;
    private Boolean moreRecords;

}
