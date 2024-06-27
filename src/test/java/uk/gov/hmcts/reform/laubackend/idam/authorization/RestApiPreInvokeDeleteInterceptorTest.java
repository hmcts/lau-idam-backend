package uk.gov.hmcts.reform.laubackend.idam.authorization;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestApiPreInvokeDeleteInterceptorTest {

    final RestApiPreInvokeDeleteInterceptor restApiPreInvokeDeleteInterceptor =
            new RestApiPreInvokeDeleteInterceptor();

    @Test
    void shouldAllowToDeleteRecord() {
        setField(restApiPreInvokeDeleteInterceptor, "allowDeleteRecord", "true");
        final boolean isRecordDeleteAllowed = restApiPreInvokeDeleteInterceptor
                .preHandle(mock(HttpServletRequest.class),
                        mock(HttpServletResponse.class),
                        mock(Object.class));

        assertThat(isRecordDeleteAllowed).isTrue();
    }

    @Test
    void shouldNotAllowToDeleteRecord() {
        setField(restApiPreInvokeDeleteInterceptor, "allowDeleteRecord", "false");

        final boolean isRecordDeleteAllowed = restApiPreInvokeDeleteInterceptor
                .preHandle(mock(HttpServletRequest.class),
                        mock(HttpServletResponse.class),
                        mock(Object.class));

        assertThat(isRecordDeleteAllowed).isFalse();
    }
}
