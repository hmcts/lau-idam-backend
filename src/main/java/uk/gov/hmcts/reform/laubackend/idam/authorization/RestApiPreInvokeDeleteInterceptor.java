package uk.gov.hmcts.reform.laubackend.idam.authorization;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
