package uk.gov.hmcts.reform.laubackend.idam.repository;

import lombok.SneakyThrows;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import static com.gilecode.reflection.ReflectionAccessUtils.getReflectionAccessor;
import static java.lang.reflect.Proxy.getInvocationHandler;

@Component
@SuppressWarnings({"all"})
public class RemoveColumnTransformers implements HibernatePropertiesCustomizer {

    @SneakyThrows
    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        final Field emailAddressField = IdamLogonAudit.class.getDeclaredField("emailAddress");
        final Field ipAddressField = IdamLogonAudit.class.getDeclaredField("ipAddress");
        final Field idField = IdamLogonAudit.class.getDeclaredField("id");


        final ColumnTransformer emailAddressColumnTransformer = emailAddressField
                .getDeclaredAnnotation(ColumnTransformer.class);
        final ColumnTransformer ipAddressColumnTransformer = ipAddressField
                .getDeclaredAnnotation(ColumnTransformer.class);

        final GeneratedValue idGeneratedValue = idField
                .getDeclaredAnnotation(GeneratedValue.class);

        updateAnnotationValue(idGeneratedValue, "strategy", GenerationType.AUTO);

        updateAnnotationValue(emailAddressColumnTransformer, "read", "");
        updateAnnotationValue(emailAddressColumnTransformer, "write", "");
        updateAnnotationValue(ipAddressColumnTransformer, "read", "");
        updateAnnotationValue(ipAddressColumnTransformer, "write", "");
    }

    @SuppressWarnings({"unchecked"})
    private void updateAnnotationValue(final Annotation annotation,
                                       final String annotationProperty,
                                       final Object value) throws Exception {
        final Object handler = getInvocationHandler(annotation);
        final Field memberValuesField = handler.getClass().getDeclaredField("memberValues");

        getReflectionAccessor().makeAccessible(memberValuesField);

        final Map<String, Object> memberValues = (Map<String, Object>) memberValuesField.get(handler);

        memberValues.put(annotationProperty, value);
    }
}