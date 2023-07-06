package uk.gov.hmcts.reform.laubackend.idam.pacttests;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslJsonRootValue;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.idam.client.models.TokenRequest;
import uk.gov.hmcts.reform.idam.client.models.TokenResponse;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@SuppressWarnings({"unchecked", "PMD.AvoidDuplicateLiterals"})
public class IdamApiConsumerTest extends IdamConsumerTestBase {

    @Pact(provider = "idamApi_oidc", consumer = "lauApi")
    public V4Pact generatePactForToken(PactBuilder builder) {

        Map<String, String> responseheaders = ImmutableMap.<String, String>builder()
            .put("Content-Type", "application/json")
            .build();

        return builder
            .usingLegacyDsl()
            .given("a token is requested")
            .uponReceiving("Provider receives a POST /o/token request from LAU API")
            .path("/o/token")
            .method(HttpMethod.POST.toString())
            .body(
                "redirect_uri=http%3A%2F%2Fwww.dummy-pact-service.com%2Fcallback"
                    + "&client_id=lau"
                    + "&grant_type=password"
                    + "&username=" + lauUsername
                    + "&password=" + lauPassword
                    + "&client_secret=" + clientSecret
                    + "&scope=openid profile roles",
                APPLICATION_FORM_URLENCODED_VALUE
            )
            .willRespondWith()
            .status(HttpStatus.OK.value())
            .headers(responseheaders)
            .body(createAuthResponse())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "generatePactForToken")
    public void verifyIdamUserDetailsRolesPactToken() {

        TokenResponse token = idamApi.generateOpenIdToken(buildTokenRequestMap());
        assertNotNull("Token is expected", token.accessToken);
    }

    private TokenRequest buildTokenRequestMap() {
        return new TokenRequest(
            "lau",
            clientSecret,
            "password",
            "http://www.dummy-pact-service.com/callback",
            lauUsername,
            lauPassword,
            "openid profile roles",
            null, null
        );
    }

    private PactDslJsonBody createUserDetailsResponse() {

        return new PactDslJsonBody()
            .stringType("sub", "61")
            .stringType("uid", "lautest@hmcts.net")
            .stringType("givenName", "Test")
            .stringType("familyName", "Lau")
            .minArrayLike("roles", 1, PactDslJsonRootValue.stringType("user"), 1);
    }

    private PactDslJsonBody createAuthResponse() {
        return new PactDslJsonBody()
            .stringType("access_token", "eyJ0eXAiOiJKV1QiLCJraWQiOiJiL082T3ZWdjEre")
            .stringType("scope", "openid roles profile");
    }
}
