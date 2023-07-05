package uk.gov.hmcts.reform.laubackend.idam.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.laubackend.idam.domain.UserDeletionAudit;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogGetRequestParams;
import uk.gov.hmcts.reform.laubackend.idam.repository.UserDeletionAuditFindRepository;
import uk.gov.hmcts.reform.laubackend.idam.repository.UserDeletionAuditInsertRepository;
import uk.gov.hmcts.reform.laubackend.idam.repository.UserDeletionAuditRepository;
import uk.gov.hmcts.reform.laubackend.idam.response.UserDeletionGetResponse;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserDeletionAuditServiceTest {

    private static final String TIMESTAMP_STR = "2023-05-23T05:32:32.345T";
    @Mock
    private UserDeletionAuditRepository userDeletionAuditRepository;

    @Mock
    private UserDeletionAuditInsertRepository userDeletionAuditInsertRepository;

    @Mock
    private UserDeletionAuditFindRepository userDeletionAuditFindRepository;

    @Mock
    private TimestampUtil timestampUtil;

    @InjectMocks
    private UserDeletionAuditService userDeletionAuditService;

    @Test
    void shouldGetUserDeletionAuditRepositoryResponse() {
        final List<UserDeletionAudit> userDeletions = Arrays.asList(getUserDeletionAudit());
        final Page<UserDeletionAudit> results = new PageImpl<>(userDeletions);
        when(userDeletionAuditFindRepository.findUserDeletion(any(), any(), any()))
            .thenReturn(results);
        final DeletionLogGetRequestParams params = new DeletionLogGetRequestParams(
            "", "", "", "", "", "", "10000", "1");

        final UserDeletionGetResponse response = userDeletionAuditService.getUserDeletions(params);

        var pageable = PageRequest.of(0, parseInt("10000"), Sort.by(Sort.Direction.DESC, "deletion_timestamp"));
        verify(userDeletionAuditFindRepository, times(1))
            .findUserDeletion(params, null, pageable);
        assertThat(response.getDeletionLogs()).hasSize(1);
        var returned = response.getDeletionLogs().get(0);
        assertEquals("1", returned.getUserId(), "UserId mismatch");
        assertEquals("email", returned.getEmailAddress(), "email mismatch");
    }

    @Test
    void shouldSaveUserDeletionsUsingEncryption() {
        ReflectionTestUtils.setField(userDeletionAuditService, "encryptionEnabled", true);
        ReflectionTestUtils.setField(userDeletionAuditService, "securityDbBackendEncryptionKey", "123");

        final Timestamp timestamp = Timestamp.valueOf(now());

        final UserDeletionAudit userDeletionAudit1 = UserDeletionAudit.builder()
            .id(1L)
            .userId("123")
            .emailAddress("email@example.net")
            .firstName("First Name")
            .lastName("Last Name")
            .timestamp(timestamp)
            .build();
        final UserDeletionAudit userDeletionAudit2 = UserDeletionAudit.builder()
            .id(2L)
            .userId("234")
            .emailAddress("email2@example.net")
            .firstName("First Name 2")
            .lastName("Last Name 2")
            .timestamp(timestamp)
            .build();

        final DeletionLog deletionLog = new DeletionLog("1", "2", "3", "4", TIMESTAMP_STR);

        when(userDeletionAuditInsertRepository.saveUserDeleteAuditWithEncryption(any(), any()))
            .thenReturn(userDeletionAudit1, userDeletionAudit2);

        final var response = userDeletionAuditService.saveUserDeletion(Arrays.asList(deletionLog, deletionLog));

        verify(userDeletionAuditInsertRepository, times(2)).saveUserDeleteAuditWithEncryption(any(), eq("123"));
        assertThat(response).hasSize(2);
        assertEquals("234", response.get(1).getUserId(), "User ids is not what was expected");
        assertEquals(
            "email2@example.net",
            response.get(1).getEmailAddress(),
            "User email is not what was expected"
        );
        assertEquals(
            "First Name 2",
            response.get(1).getFirstName(),
            "First name is not what was expected"
        );
        assertEquals(
            "Last Name 2",
            response.get(1).getLastName(),
            "Last name is not what was expected");
        assertEquals(
            new TimestampUtil().timestampConvertor(timestamp),
            response.get(1).getDeletionTimestamp(),
            "Timestamps do not match"
        );
    }

    @Test
    void shouldSaveUserDeletion() {
        final Timestamp timestamp = Timestamp.valueOf(now());

        final UserDeletionAudit userDeletionAudit = UserDeletionAudit.builder()
            .id(1L)
            .userId("456")
            .emailAddress("email@example.net")
            .firstName("First Name")
            .lastName("Last Name")
            .timestamp(timestamp)
            .build();

        final DeletionLog deletionLog = new DeletionLog("1", "2", "3", "4", TIMESTAMP_STR);

        when(userDeletionAuditRepository.save(any())).thenReturn(userDeletionAudit);

        final var response = userDeletionAuditService.saveUserDeletion(Arrays.asList(deletionLog));
        verify(userDeletionAuditRepository, times(1)).save(any());
        assertThat(response).hasSize(1);
        assertEquals("456", response.get(0).getUserId(), "User ids are not equal");
        assertEquals("email@example.net", response.get(0).getEmailAddress(), "Emails are not equal");
        assertEquals("First Name", response.get(0).getFirstName(), "First names are not equal");
        assertEquals("Last Name", response.get(0).getLastName(), "Last names are not equal");
        String timestampStr = new TimestampUtil().timestampConvertor(timestamp);
        assertEquals(timestampStr, response.get(0).getDeletionTimestamp(), "Timestamps are not equal");
    }

    @Test
    void shouldDeleteUserDeletion() {
        userDeletionAuditService.deleteUserDeletionByUserId("1");
        verify(userDeletionAuditRepository, times(1)).deleteUserDeletionAuditByUserId("1");
    }

    private UserDeletionAudit getUserDeletionAudit() {
        return UserDeletionAudit.builder()
            .userId("1")
            .emailAddress("email")
            .firstName("John")
            .lastName("Smith")
            .timestamp(Timestamp.from(Instant.now()))
            .build();
    }
}
