package uk.gov.hmcts.reform.laubackend.idam.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDeletionPostRequest {
    List<DeletionLog> deletionLogs;
}
