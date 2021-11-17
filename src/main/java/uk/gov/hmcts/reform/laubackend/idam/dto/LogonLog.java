package uk.gov.hmcts.reform.laubackend.idam.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ApiModel(description = "Data model for the IdAm logon request")
public class LogonLog {
    private String userId;
    private String emailAddress;
    private String service;
    private String ipAddress;
    private String timestamp;
}
