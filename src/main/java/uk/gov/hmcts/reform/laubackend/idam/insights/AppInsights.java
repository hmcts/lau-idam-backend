package uk.gov.hmcts.reform.laubackend.idam.insights;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppInsights implements EventRepository {

    private final TelemetryClient telemetry;

    @Autowired
    public AppInsights(TelemetryClient telemetry) {
        telemetry.getContext().getComponent().setVersion(getClass().getPackage().getImplementationVersion());
        this.telemetry = telemetry;
    }

    @Override
    public void trackEvent(String name, Map<String, String> properties) {
        telemetry.trackEvent(name, properties,null);
    }

    public Map<String, String> trackingMap(String propertyName, String propertyToTrack) {
        Map<String, String> trackMap = new ConcurrentHashMap<>();
        trackMap.put(propertyName, propertyToTrack);
        return trackMap;
    }
}
