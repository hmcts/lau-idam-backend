package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;


@JsonInclude(JsonInclude.Include.NON_NULL)

@Generated("jsonschema2pojo")
public class LogOnRequestVO {

    @JsonProperty("logonLog")
    private LogonLog logonLog;

    public LogonLog getLogonLog() {
        return logonLog;
    }

    public void setLogonLog(LogonLog logonLog) {
        this.logonLog = logonLog;
    }

}
