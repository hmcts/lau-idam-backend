package uk.gov.hmcts.reform.laubackend.idam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data model for the IdAM logon request")
public class LogonLog implements Serializable {

    public static final long serialVersionUID = 432973388;

    @Schema(description = "The user on whose behalf the logon took place")
    private String userId;

    @Schema(description = "The email address of the logon user")
    private String emailAddress;

    @Schema(description = "The service the logon user is accessing")
    private String service;

    @Schema(description = "The client ip address of the logon user")
    private String ipAddress;

    @Schema(description = "Idam login state")
    private String loginState;

    @Schema(description = "When the operation took place with microseconds in iso-8601-date-and-time-format")
    private String timestamp;

    public LogonLog toDto(final IdamLogonAudit idamLogonAudit, final String timestamp) {
        this.userId = idamLogonAudit.getUserId();
        this.emailAddress = idamLogonAudit.getEmailAddress();
        this.service = idamLogonAudit.getService();
        this.ipAddress = idamLogonAudit.getIpAddress();
        this.loginState = idamLogonAudit.getLoginState();
        this.timestamp = timestamp;

        return this;
    }
}
