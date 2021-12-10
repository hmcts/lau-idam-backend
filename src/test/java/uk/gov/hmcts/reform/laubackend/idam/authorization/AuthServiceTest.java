package uk.gov.hmcts.reform.laubackend.idam.authorization;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserInfo;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidAuthorizationException;
import uk.gov.hmcts.reform.laubackend.idam.exceptions.InvalidServiceAuthorizationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthServiceTest {

    private static final String LAU_IDAM_BACKEND_SERVICE_AUTH = "DFJSDFSDFSDFSDFSDSFS";
    private static final String LAU_IDAM_BACKEND_SERVICE_NAME = "lau_idam_backend";

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthTokenValidator authTokenValidator;

    @Mock
    private IdamClient idamClient;

    @BeforeEach
    void setUp() {
        this.authService = new AuthService(authTokenValidator, idamClient);
    }

    @Test
    void testShouldGetServiceAuthName() {
        when(authTokenValidator.getServiceName(LAU_IDAM_BACKEND_SERVICE_AUTH))
                .thenReturn(LAU_IDAM_BACKEND_SERVICE_NAME);

        final String actualServiceName = authService.authenticateService(LAU_IDAM_BACKEND_SERVICE_AUTH);

        assertNotNull(actualServiceName, "Should be not null");
        assertEquals(LAU_IDAM_BACKEND_SERVICE_NAME,
                actualServiceName, "Should return authenticated service name");
    }

    @Test
    void testShouldErrorIfServiceNotAuthenticated() {
        try {
            authService.authenticateService(null);
            fail("The method should have thrown InvalidAuthenticationException");
        } catch (InvalidServiceAuthorizationException iae) {
            assertThat(iae.getMessage()).isEqualTo("Missing ServiceAuthorization header");
        }
    }

    @Test
    void testShouldErrorIfAuthorizationHeaderNull() {
        try {
            authService.authorize(null);
            fail("The method should have thrown InvalidAuthorizationException");
        } catch (InvalidAuthorizationException iae) {
            assertThat(iae.getMessage()).isEqualTo("Missing Authorization header");
        }
    }

    @Test
    void testShouldNotThrowExceptionForValidAuth() {
        final UserInfo userInfo = mock(UserInfo.class);
        when(authService.authorize("hum_ho")).thenReturn(userInfo);

        assertDoesNotThrow(() -> authService.authorize("hum_ho"));
    }
}
