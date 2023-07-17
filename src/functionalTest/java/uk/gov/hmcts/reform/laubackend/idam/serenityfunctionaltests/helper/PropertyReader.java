package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.helper;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

    private static PropertyReader propertyReader;

    public Properties properties;

    private PropertyReader() {
        try {
            properties = new Properties();
            properties.load(PropertyReader.class.getResourceAsStream("/application-functional.yaml"));
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    public static PropertyReader getInstance() {
        if (propertyReader == null)
            propertyReader = new PropertyReader();

        return propertyReader;
    }

    public String getPropertyValue(String propertyKey) {
        return properties.getProperty(propertyKey);
    }
}
