package uk.gov.hmcts.reform.laubackend.idam.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.gov.hmcts.reform.laubackend.idam.Application;
import uk.gov.hmcts.reform.laubackend.idam.config.IntegrationJwtTestConfig;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Cucumber Spring context bootstrap class.
 * Intentionally contains no test methods.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@Import(IntegrationJwtTestConfig.class)
@Slf4j
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "java:S2187"})
public class SpringBootIntegrationTest {
}
