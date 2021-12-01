package uk.gov.hmcts.reform.laubackend.idam.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import uk.gov.hmcts.reform.laubackend.idam.utils.FlywayNoOpStrategy;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FlywayConfigurationTest {

    private final FlywayConfiguration flywayConfiguration = new FlywayConfiguration();


    @Test
    void shouldReturnInstanceOfFlywayMigrationStrategy() {
        assertThat(flywayConfiguration.flywayMigrationStrategy()).isInstanceOf(FlywayMigrationStrategy.class);
        assertThat(flywayConfiguration.flywayMigrationStrategy()).isInstanceOf(FlywayNoOpStrategy.class);
    }

    @Test
    void shouldReturnNullFlyWayMigrationStrategy() {
        assertThat(flywayConfiguration.flywayVoidMigrationStrategy()).isNull();
    }
}
