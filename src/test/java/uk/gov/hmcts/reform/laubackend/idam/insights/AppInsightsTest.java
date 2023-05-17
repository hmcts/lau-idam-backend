package uk.gov.hmcts.reform.laubackend.idam.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.TelemetryContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.laubackend.idam.insights.AppInsightsEvent.GET_LOGON_REQUEST_INVALID_REQUEST_EXCEPTION;

class AppInsightsTest {
    private AppInsights classUnderTest;

    @Mock
    private TelemetryClient telemetryClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        TelemetryContext telemetryContext = new TelemetryContext();
        doReturn(telemetryContext).when(telemetryClient).getContext();
        classUnderTest = new AppInsights(telemetryClient);
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
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void testTelemetry() {
        TelemetryContext telemetryContext = new TelemetryContext();

        TelemetryClient telemetryClient = mock(TelemetryClient.class);
        when(telemetryClient.getContext()).thenReturn(telemetryContext);

        AppInsights appInsights = new AppInsights(telemetryClient);

        Assert.isInstanceOf(AppInsights.class, appInsights);
    }
}
