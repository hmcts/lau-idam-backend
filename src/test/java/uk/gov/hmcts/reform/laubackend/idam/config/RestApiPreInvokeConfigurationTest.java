package uk.gov.hmcts.reform.laubackend.idam.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestApiPreInvokeConfigurationTest {

    @Test
    void shouldAddRestApiPreInvokeInterceptor() {
        final RestApiPreInvokeConfiguration restApiPreInvokeConfiguration = new RestApiPreInvokeConfiguration();
        final InterceptorRegistry interceptorRegistry = mock(InterceptorRegistry.class);

        when(interceptorRegistry.addInterceptor(any())).thenReturn(mock(InterceptorRegistration.class));

        restApiPreInvokeConfiguration.addInterceptors(interceptorRegistry);

        verify(interceptorRegistry, times(2)).addInterceptor(any());
    }
}