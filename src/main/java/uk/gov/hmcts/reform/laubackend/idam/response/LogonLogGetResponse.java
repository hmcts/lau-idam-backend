package uk.gov.hmcts.reform.laubackend.idam.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.reform.laubackend.idam.dto.LogonLog;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Schema(description = "IdAM Logon GET Response")
public class LogonLogGetResponse implements Serializable {

    public static final long serialVersionUID = 432973322;

    private List<LogonLog> logonLog;

    @Schema(description = "The index of the first record out of the full result set provided in the result set")
    private int startRecordNumber;

    @Schema(description = "Indicates whether there are more records beyond the current page in the full result set")
    private boolean moreRecords;

    @Schema(description = "The total number of records in the full result set")
    private long totalNumberOfRecords;

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

    public LogonLogGetResponse withTotalNumberOfRecords(final long totalNumberOfRecords) {
        this.totalNumberOfRecords = totalNumberOfRecords;
        return this;
    }


    public LogonLogGetResponse build() {
        final LogonLogGetResponse logonLogGetResponse = new LogonLogGetResponse();
        logonLogGetResponse.setLogonLog(logonLog);
        logonLogGetResponse.setStartRecordNumber(startRecordNumber);
        logonLogGetResponse.setMoreRecords(moreRecords);
        logonLogGetResponse.setTotalNumberOfRecords(totalNumberOfRecords);
        return logonLogGetResponse;
    }
}
