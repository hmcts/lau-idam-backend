package uk.gov.hmcts.reform.laubackend.idam.controllers;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.annotations.WithTags;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.laubackend.idam.client.LauIdamBackEndServiceClient;

@ExtendWith({SpringExtension.class, SerenityJUnit5Extension.class})
@WithTags({@WithTag("testType:Functional")})
@ComponentScan("uk.gov.hmcts.reform.laubackend.idam")
@TestPropertySource("classpath:application-functional.yaml")
@Slf4j
@SuppressWarnings({"PMD.AbstractClassWithoutAbstractMethod"})
abstract class LauIdamBaseFunctionalTest {

    @Value("${targetInstance}")
    protected String lauIdamBackEndApiUrl;

    protected LauIdamBackEndServiceClient lauIdamBackEndServiceClient;

    @BeforeEach
    void setUp() {
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.defaultParser = Parser.JSON;

        lauIdamBackEndServiceClient = new LauIdamBackEndServiceClient(lauIdamBackEndApiUrl);
    }
}
