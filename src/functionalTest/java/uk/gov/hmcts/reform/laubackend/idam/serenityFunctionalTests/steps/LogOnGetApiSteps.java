package uk.gov.hmcts.reform.laubackend.idam.serenityFunctionalTests.steps;

import io.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import org.junit.Assert;
import uk.gov.hmcts.reform.laubackend.idam.serenityFunctionalTests.utils.TestConstants;

public class LogOnGetApiSteps extends BaseSteps {

    @Step("Given a valid service token is generated")
    public String givenAValidServiceTokenIsGenerated() {
        return getServiceToken(TestConstants.S2S_NAME);
    }

    @Step("Then a success response is returned")
    public String thenASuccessResposeIsReturned(Response response) {
        Assert.assertTrue(
            "Response status code is not 200, but it is " + response.getStatusCode(),
            response.statusCode() == 200 || response.statusCode() == 201
        );
        return TestConstants.SUCCESS;
    }

}
