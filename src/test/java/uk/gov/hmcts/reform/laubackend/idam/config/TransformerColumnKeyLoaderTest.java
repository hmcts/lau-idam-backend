package uk.gov.hmcts.reform.laubackend.idam.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

import java.lang.reflect.Field;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
class TransformerColumnKeyLoaderTest {

    TransformerColumnKeyLoader transformerColumnKeyLoader;
    ApplicationPreparedEvent mockApplicationPreparedEvent;
    ConfigurableEnvironment mockEnvironment;
    MutablePropertySources mockMutablePropertySources;
    PropertySource mockPropertySource;
    ConfigurableApplicationContext mockApplicationContext;

    private static final String PROPERTY_NAME_1 = "security.db.backend-encryption-key";
    private static final String PROPERTY_NAME_2 = "backend-encryption-key";
    private static final String PROPERTY_NAME_3 = "security.db.encryption-enabled";
    private static final String ENCRYPTION_KEY = "Unit_Test_Key";
    private static final String ANNOTATION_ASSERT_MSG = "Annotation is not correct";
    private static final String FIELD_NAME_EMAIL_ADDRESS = "emailAddress";
    private static final String FIELD_NAME_IP_ADDRESS = "ipAddress";

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        mockApplicationPreparedEvent = mock(ApplicationPreparedEvent.class);
        mockEnvironment = mock(ConfigurableEnvironment.class);
        mockApplicationContext = mock(ConfigurableApplicationContext.class);
        when(mockApplicationPreparedEvent.getApplicationContext()).thenReturn(mockApplicationContext);
        when(mockApplicationContext.getEnvironment()).thenReturn(mockEnvironment);
        mockPropertySource = mock(PropertySource.class);
        when(mockEnvironment.getProperty(PROPERTY_NAME_1)).thenReturn(ENCRYPTION_KEY);
        PropertySources mockPropertySources = mock(PropertySources.class);
        Iterator<PropertySource<?>> mockIterator = mock(Iterator.class);
        when(mockIterator.next()).thenReturn(mockPropertySource);
        when(mockPropertySources.iterator()).thenReturn(mockIterator);
        mockMutablePropertySources = new MutablePropertySources(mockPropertySources);
        mockMutablePropertySources.addFirst(mockPropertySource);
        when(mockEnvironment.getPropertySources()).thenReturn(mockMutablePropertySources);
    }

    @Test
    void testAnnotationUpdatedWithEncryption() throws NoSuchFieldException {

        transformerColumnKeyLoader = new TransformerColumnKeyLoader(TestIdamLogonAudit.class);
        when(mockPropertySource.containsProperty(PROPERTY_NAME_1)).thenReturn(true);
        when(mockEnvironment.getProperty(PROPERTY_NAME_3)).thenReturn("Yes");

        transformerColumnKeyLoader.onApplicationEvent(mockApplicationPreparedEvent);

        Field fieldEmailAddress = IdamLogonAudit.class.getDeclaredField(FIELD_NAME_EMAIL_ADDRESS);
        ColumnTransformer columnTransformer1 = fieldEmailAddress.getDeclaredAnnotation(ColumnTransformer.class);
        assertFalse(columnTransformer1.read().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);
        assertFalse(columnTransformer1.write().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);

        fieldEmailAddress = TestIdamLogonAudit.class.getDeclaredField(FIELD_NAME_EMAIL_ADDRESS);
        columnTransformer1 = fieldEmailAddress.getDeclaredAnnotation(ColumnTransformer.class);
        assertTrue(columnTransformer1.read().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);
        assertTrue(columnTransformer1.write().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);

        Field fieldIpAddress = IdamLogonAudit.class.getDeclaredField(FIELD_NAME_IP_ADDRESS);
        ColumnTransformer columnTransformer2 = fieldIpAddress.getDeclaredAnnotation(ColumnTransformer.class);
        assertFalse(columnTransformer2.read().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);
        assertFalse(columnTransformer2.write().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);

        fieldIpAddress = TestIdamLogonAudit.class.getDeclaredField(FIELD_NAME_IP_ADDRESS);
        columnTransformer2 = fieldIpAddress.getDeclaredAnnotation(ColumnTransformer.class);
        assertTrue(columnTransformer2.read().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);
        assertTrue(columnTransformer2.write().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);

        verify(mockApplicationPreparedEvent, times(1)).getApplicationContext();
        verify(mockApplicationContext, times(1)).getEnvironment();
        verify(mockEnvironment, times(1)).getPropertySources();
        verify(mockPropertySource, times(1)).containsProperty(PROPERTY_NAME_1);
        verify(mockEnvironment, times(1)).getProperty(PROPERTY_NAME_1);
    }

    @Test
    void testAnnotationUpdatedWithoutEncryption() throws NoSuchFieldException {

        transformerColumnKeyLoader = new TransformerColumnKeyLoader(TestIdamLogonAudit2.class);
        when(mockPropertySource.containsProperty(PROPERTY_NAME_1)).thenReturn(true);
        when(mockEnvironment.getProperty(PROPERTY_NAME_3)).thenReturn("No");

        transformerColumnKeyLoader.onApplicationEvent(mockApplicationPreparedEvent);

        Field field = IdamLogonAudit.class.getDeclaredField(FIELD_NAME_EMAIL_ADDRESS);
        ColumnTransformer columnTransformer = field.getDeclaredAnnotation(ColumnTransformer.class);
        assertFalse(columnTransformer.read().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);
        assertFalse(columnTransformer.write().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);

        field = TestIdamLogonAudit2.class.getDeclaredField(FIELD_NAME_EMAIL_ADDRESS);
        columnTransformer = field.getDeclaredAnnotation(ColumnTransformer.class);
        assertEquals("email_address", columnTransformer.read(), ANNOTATION_ASSERT_MSG);
        assertEquals("?", columnTransformer.write(), ANNOTATION_ASSERT_MSG);

        verify(mockApplicationPreparedEvent, times(1)).getApplicationContext();
        verify(mockApplicationContext, times(1)).getEnvironment();
        verify(mockEnvironment, times(1)).getPropertySources();
        verify(mockPropertySource, times(1)).containsProperty(PROPERTY_NAME_1);
        verify(mockEnvironment, times(1)).getProperty(PROPERTY_NAME_1);
    }

    @Test
    void testAnnotationUpdatedAlternativeProperty() throws NoSuchFieldException {

        transformerColumnKeyLoader = new TransformerColumnKeyLoader(TestIdamLogonAudit.class);
        when(mockPropertySource.containsProperty(PROPERTY_NAME_2)).thenReturn(true);
        when(mockEnvironment.getProperty(PROPERTY_NAME_3)).thenReturn("Yes");

        transformerColumnKeyLoader.onApplicationEvent(mockApplicationPreparedEvent);

        Field field = IdamLogonAudit.class.getDeclaredField(FIELD_NAME_EMAIL_ADDRESS);
        ColumnTransformer columnTransformer = field.getDeclaredAnnotation(ColumnTransformer.class);
        assertFalse(columnTransformer.read().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);
        assertFalse(columnTransformer.write().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);

        field = TestIdamLogonAudit.class.getDeclaredField(FIELD_NAME_EMAIL_ADDRESS);
        columnTransformer = field.getDeclaredAnnotation(ColumnTransformer.class);
        assertTrue(columnTransformer.read().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);
        assertTrue(columnTransformer.write().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);

        verify(mockApplicationPreparedEvent, times(1)).getApplicationContext();
        verify(mockApplicationContext, times(1)).getEnvironment();
        verify(mockEnvironment, times(1)).getPropertySources();
        verify(mockPropertySource, times(1)).containsProperty(PROPERTY_NAME_1);
        verify(mockEnvironment, times(1)).getProperty(PROPERTY_NAME_1);
    }

    @Test
    void testNoSuchField() throws NoSuchFieldException {

        transformerColumnKeyLoader = new TransformerColumnKeyLoader(RandomTestClass.class);
        when(mockPropertySource.containsProperty(PROPERTY_NAME_1)).thenReturn(true);
        when(mockEnvironment.getProperty(PROPERTY_NAME_3)).thenReturn("Yes");

        try {
            transformerColumnKeyLoader.onApplicationEvent(mockApplicationPreparedEvent);
        } catch (IllegalStateException ise) {
            assertTrue(ise.getMessage().contains("Encryption key cannot be loaded into"), "Not valid exception");
        }

        Field field = IdamLogonAudit.class.getDeclaredField(FIELD_NAME_EMAIL_ADDRESS);
        ColumnTransformer columnTransformer = field.getDeclaredAnnotation(ColumnTransformer.class);
        assertFalse(columnTransformer.read().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);
        assertFalse(columnTransformer.write().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);


        verify(mockApplicationPreparedEvent, times(1)).getApplicationContext();
        verify(mockApplicationContext, times(1)).getEnvironment();
        verify(mockEnvironment, times(1)).getPropertySources();
        verify(mockPropertySource, times(1)).containsProperty(PROPERTY_NAME_1);
        verify(mockEnvironment, times(1)).getProperty(PROPERTY_NAME_1);

    }

    @Test
    void testMemberValueAccessError() throws NoSuchFieldException {

        transformerColumnKeyLoader = new TransformerColumnKeyLoader(RandomTestClass2.class);
        when(mockPropertySource.containsProperty(PROPERTY_NAME_1)).thenReturn(true);
        when(mockEnvironment.getProperty(PROPERTY_NAME_3)).thenReturn("No");

        try {
            transformerColumnKeyLoader.onApplicationEvent(mockApplicationPreparedEvent);
        } catch (IllegalStateException ise) {
            assertNotNull(ise.getMessage(), "Not valid exception");
        }

        Field field = IdamLogonAudit.class.getDeclaredField(FIELD_NAME_EMAIL_ADDRESS);
        ColumnTransformer columnTransformer = field.getDeclaredAnnotation(ColumnTransformer.class);
        assertFalse(columnTransformer.read().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);
        assertFalse(columnTransformer.write().contains(ENCRYPTION_KEY), ANNOTATION_ASSERT_MSG);

        verify(mockApplicationPreparedEvent, times(1)).getApplicationContext();
        verify(mockApplicationContext, times(1)).getEnvironment();
        verify(mockEnvironment, times(1)).getPropertySources();
        verify(mockPropertySource, times(1)).containsProperty(PROPERTY_NAME_1);
        verify(mockEnvironment, times(1)).getProperty(PROPERTY_NAME_1);
    }

}
