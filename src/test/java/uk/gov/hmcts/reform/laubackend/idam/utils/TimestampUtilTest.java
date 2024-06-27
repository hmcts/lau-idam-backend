package uk.gov.hmcts.reform.laubackend.idam.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Timestamp;

import static java.sql.Timestamp.valueOf;
import static java.time.LocalDateTime.parse;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimestampUtilTest {

    @Test
    void shouldCovertToTimestamp() {
        final TimestampUtil timestampUtil = new TimestampUtil();
        final Timestamp timestamp = valueOf(parse("2000-08-23T22:20:05"));
        final String convertedTimestamp = timestampUtil.timestampConvertor(timestamp);

        assertThat(convertedTimestamp).isEqualTo("2000-08-23T22:20:05.000Z");
    }

    @Test
    void shouldReturnTimestampFromString() {
        final TimestampUtil timestampUtil = new TimestampUtil();
        final String stringTimestamp = "2000-08-23T22:20:05.200";
        final Timestamp convertedTimestamp = timestampUtil.getTimestampValue(stringTimestamp);

        assertThat(convertedTimestamp).hasToString("2000-08-23 22:20:05.2");
    }

    @Test
    void shouldReturnUtcTimestampFromString() {
        final TimestampUtil timestampUtil = new TimestampUtil();
        final String stringTimestamp = "2000-08-23T22:20:05.200Z";
        final Timestamp convertedTimestamp = timestampUtil.getUtcTimestampValue(stringTimestamp);

        assertThat(convertedTimestamp).hasToString("2000-08-23 22:20:05.2");
    }
}
