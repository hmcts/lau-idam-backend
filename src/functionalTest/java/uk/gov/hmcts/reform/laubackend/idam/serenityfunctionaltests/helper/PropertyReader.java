package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.helper;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public final class PropertyReader {

    private static PropertyReader reader;

    public Properties properties;

    private PropertyReader() {
        try {
            properties = new Properties();
            properties.load(PropertyReader.class.getResourceAsStream("/application-functional.yaml"));
        } catch (final IOException ioException) {
            log.error(ioException.getMessage(), ioException);
        }
    }

    public static PropertyReader getInstance() {

        synchronized (PropertyReader.class) {
            if (reader == null) {
                reader = new PropertyReader();
            }
            return reader;
        }
    }

    public String getPropertyValue(String propertyKey) {
        return properties.getProperty(propertyKey);
    }
}
