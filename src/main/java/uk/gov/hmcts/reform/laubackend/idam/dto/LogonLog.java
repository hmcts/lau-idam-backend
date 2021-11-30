package uk.gov.hmcts.reform.laubackend.idam.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Data model for the IdAM logon request")
public class LogonLog implements Serializable {

    public static final long serialVersionUID = 432973388;

    @ApiModelProperty(notes = "The user on whose behalf the logon took place")
    private String userId;

    @ApiModelProperty(notes = "The email address of the logon user")
    private String emailAddress;

    @ApiModelProperty(notes = "The service the logon user is accessing")
    private String service;

    @ApiModelProperty(notes = "The client ip address of the logon user")
    private String ipAddress;

    @ApiModelProperty(notes = "When the operation took place with microseconds in iso-8601-date-and-time-format")
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
