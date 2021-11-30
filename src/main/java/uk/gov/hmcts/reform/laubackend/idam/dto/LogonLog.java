package uk.gov.hmcts.reform.laubackend.idam.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ApiModel(description = "Data model for the IdAm logon request")
public class LogonLog implements Serializable {
    private String userId;
    private String emailAddress;
    private String service;
    private String ipAddress;
    private String timestamp;

    public LogonLog toDto(final IdamLogonAudit idamLogonAudit, final String timestamp) {
        this.userId = idamLogonAudit.getUserId();
        this.emailAddress = idamLogonAudit.getEmailAddress();
        this.service = idamLogonAudit.getService();
        this.ipAddress = idamLogonAudit.getIpAddress();
        this.timestamp = timestamp;

        return this;
    }
}
