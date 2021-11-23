package uk.gov.hmcts.reform.laubackend.idam.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LogonLogPostRequest {
    private LogonLog logonLog;
}
