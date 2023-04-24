package uk.gov.hmcts.reform.laubackend.idam.authorization;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

public class RestApiPreInvokeDeleteInterceptor implements HandlerInterceptor {

    @Value("${db.allow.delete.record}")
    private String allowDeleteRecord;

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) {
        return allowDeleteRecord != null && allowDeleteRecord.equals("true");
    }
}
