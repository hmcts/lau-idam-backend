package uk.gov.hmcts.reform.laubackend.idam.bdd;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.laubackend.idam.Application;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@CucumberContextConfiguration
@CucumberOptions(features = "src/integration/resources")
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@Slf4j
@SuppressWarnings({"PMD.AbstractClassWithoutAnyMethod", "PMD.AbstractClassWithoutAbstractMethod"})
public abstract class SpringBootIntegrationTest {
}
