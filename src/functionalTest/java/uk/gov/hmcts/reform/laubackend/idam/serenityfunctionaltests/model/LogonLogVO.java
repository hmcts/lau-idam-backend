package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LogonLogVO {

    public static final long serialVersionUID = 432973322;

    private String id;
    private String userId;
    private String emailAddress;
    private String service;
    private String ipAddress;
    private String timestamp;
}