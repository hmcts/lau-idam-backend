package uk.gov.hmcts.reform.laubackend.idam.controllers;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.laubackend.idam.client.LauIdamBackEndServiceClient;

@RunWith(SpringIntegrationSerenityRunner.class)
@WithTags({@WithTag("testType:Functional")})
@ComponentScan("uk.gov.hmcts.reform.laubackend.idam")
@TestPropertySource("classpath:application-functional.yaml")
@Slf4j
@SuppressWarnings({"PMD.AbstractClassWithoutAbstractMethod"})
public abstract class LauIdamBaseFunctionalTest {

    @Value("${targetInstance}")
    protected String lauIdamBackEndApiUrl;

    protected LauIdamBackEndServiceClient lauIdamBackEndServiceClient;

    @Before
    public void setUp() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.defaultParser = Parser.JSON;

        lauIdamBackEndServiceClient = new LauIdamBackEndServiceClient(lauIdamBackEndApiUrl);
    }
}
