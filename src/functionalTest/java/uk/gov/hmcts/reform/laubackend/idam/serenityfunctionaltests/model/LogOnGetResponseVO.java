package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LogOnGetResponseVO {

    @JsonProperty("logonLog")
    private List<LogonLog> logonLog;
    @JsonProperty("startRecordNumber")
    private Integer startRecordNumber;
    @JsonProperty("moreRecords")
    private Boolean moreRecords;

    @JsonProperty("totalNumberOfRecords")
    private Long totalNumberOfRecords;

    @JsonProperty("logonLog")
    public List<LogonLog> getLogonLog() {
        return logonLog;
    }

    @JsonProperty("logonLog")
    public void setLogonLog(List<LogonLog> logonLog) {
        this.logonLog = logonLog;
    }

    @JsonProperty("startRecordNumber")
    public Integer getStartRecordNumber() {
        return startRecordNumber;
    }

    @JsonProperty("startRecordNumber")
    public void setStartRecordNumber(Integer startRecordNumber) {
        this.startRecordNumber = startRecordNumber;
    }

    @SuppressWarnings("PMD.BooleanGetMethodName")
    @JsonProperty("moreRecords")
    public Boolean getMoreRecords() {
        return moreRecords;
    }

    @JsonProperty("moreRecords")
    public void setMoreRecords(Boolean moreRecords) {
        this.moreRecords = moreRecords;
    }

    @JsonProperty("totalNumberOfRecords")
    public Long getTotalNumberOfRecords() {
        return totalNumberOfRecords;
    }

    @JsonProperty("totalNumberOfRecords")
    public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
        this.totalNumberOfRecords = totalNumberOfRecords;
    }

}
