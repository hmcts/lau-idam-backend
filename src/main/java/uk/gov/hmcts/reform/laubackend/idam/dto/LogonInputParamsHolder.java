package uk.gov.hmcts.reform.laubackend.idam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LogonInputParamsHolder {
    private String userId;
    private String emailAddress;
    private String startTime;
    private String endTime;
    private String size;
    private String page;
}
