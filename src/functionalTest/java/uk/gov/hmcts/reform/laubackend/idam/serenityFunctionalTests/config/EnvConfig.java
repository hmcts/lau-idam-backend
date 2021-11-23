package uk.gov.hmcts.reform.laubackend.idam.serenityFunctionalTests.config;

public final class EnvConfig {

    public static final String API_URL = System.getenv("TEST_URL");


    private EnvConfig() {

    }
}
