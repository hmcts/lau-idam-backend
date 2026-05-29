package uk.gov.hmcts.reform.laubackend.idam.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.TelemetryContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.GET_LOGON_REQUEST_INVALID_REQUEST_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class AppInsightsTest {

    @Mock
    private TelemetryClient telemetryClient;

    private AppInsights classUnderTest;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        TelemetryContext telemetryContext = new TelemetryContext();
        doReturn(telemetryContext).when(telemetryClient).getContext();
        classUnderTest = new AppInsights(telemetryClient);
    }

    @AfterEach
    void close() throws Exception {
        autoCloseable.close();
    }

    @Test
    void trackRequest() {
        try {
            classUnderTest.trackEvent(GET_LOGON_REQUEST_INVALID_REQUEST_EXCEPTION.toString(),
                                      classUnderTest.trackingMap("excepion", "Error"));
        } catch (Exception exp) {
            fail("classUnderTest.trackEvent() failed.");
        }
    }

    @Test
    void testTelemetry() {
        assertThat(classUnderTest).isInstanceOf(AppInsights.class);
    }
}
