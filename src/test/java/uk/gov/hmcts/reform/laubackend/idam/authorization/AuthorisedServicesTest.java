
package uk.gov.hmcts.reform.laubackend.idam.authorization;


import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorisedServicesTest {

    private static final String FIELD_NAME = "authorisedServicesList";
    private static final String VALID_AUTHORISED_SERVICE = "lau_idam_backend";
    private static final String INVALID_AUTHORISED_SERVICE = "made_up";
    private static final String AUTHORISED_SERVICE_SUCCESS = "Should return true for successful service.";
    private static final String AUTHORISED_SERVICE_FAILURE = "Should return false for unsuccessful service.";

    @Autowired
    private AuthorisedServices authorisedServices;

    @BeforeEach
    void setUp() throws IllegalAccessException {
        authorisedServices = new AuthorisedServices();
        final List<String> list = Arrays.asList(VALID_AUTHORISED_SERVICE);
        FieldUtils.writeField(authorisedServices, FIELD_NAME, list, true);
    }

    @Test
    void testKnownAuthorisedServiceSuccess() {
        assertTrue(authorisedServices.hasService(VALID_AUTHORISED_SERVICE), AUTHORISED_SERVICE_SUCCESS);
    }

    @Test
    void testUnknownAuthorisedService() {
        assertFalse(authorisedServices.hasService(INVALID_AUTHORISED_SERVICE), AUTHORISED_SERVICE_FAILURE);
    }
}
