package uk.gov.hmcts.reform.laubackend.idam.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UserDeletionPostResponse {
    List<DeletionLog> deletionLogs;
}
