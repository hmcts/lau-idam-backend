package uk.gov.hmcts.reform.laubackend.idam.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ApiModel(description = "IdAM Logon GET Response")
public class LogonLogGetResponse implements Serializable {

    public static final long serialVersionUID = 432973322;

    private List<LogonLog> logonLog;

    @ApiModelProperty(notes = "The index of the first record out of the full result set provided in the result set")
    private int startRecordNumber;

    @ApiModelProperty(notes = "Indicates whether there are more records beyond the current page in the full result set")
    private boolean moreRecords;

    public static LogonLogGetResponse logonLogResponse() {
        return new LogonLogGetResponse();
    }

    public LogonLogGetResponse withLogonLog(final List<LogonLog> logonLog) {
        this.logonLog = logonLog;
        return this;
    }

    public LogonLogGetResponse withStartRecordNumber(final Integer startRecordNumber) {
        this.startRecordNumber = startRecordNumber;
        return this;
    }

    public LogonLogGetResponse withMoreRecords(final boolean moreRecords) {
        this.moreRecords = moreRecords;
        return this;
    }

    public LogonLogGetResponse build() {
        final LogonLogGetResponse logonLogGetResponse = new LogonLogGetResponse();
        logonLogGetResponse.setLogonLog(logonLog);
        logonLogGetResponse.setStartRecordNumber(startRecordNumber);
        logonLogGetResponse.setMoreRecords(moreRecords);
        return logonLogGetResponse;
    }
}
