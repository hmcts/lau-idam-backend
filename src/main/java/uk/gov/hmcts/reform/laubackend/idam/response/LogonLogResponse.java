package uk.gov.hmcts.reform.laubackend.idam.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

@NoArgsConstructor
@Getter
@Setter
public class LogonLogResponse {

    public static final long serialVersionUID = 432973322;

    private String id;
    private String userId;
    private String emailAddress;
    private String service;
    private String ipAddress;
    private String timestamp;

    public LogonLogResponse toDto(final IdamLogonAudit idamLogonAudit, final String timestamp) {
        this.id = idamLogonAudit.getId().toString();
        this.userId = idamLogonAudit.getUserId();
        this.emailAddress = idamLogonAudit.getEmailAddress();
        this.service = idamLogonAudit.getService();
        this.ipAddress = idamLogonAudit.getIpAddress();
        this.timestamp = timestamp;

        return this;
    }
}
