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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.reform.laubackend.idam.domain.UserDeletionAudit;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLog;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogAllUsersRequestParams;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogGetRequestParams;
import uk.gov.hmcts.reform.laubackend.idam.repository.UserDeletionAuditFindRepository;
import uk.gov.hmcts.reform.laubackend.idam.repository.UserDeletionAuditInsertRepository;
import uk.gov.hmcts.reform.laubackend.idam.repository.UserDeletionAuditRepository;
import uk.gov.hmcts.reform.laubackend.idam.response.UserDeletionGetResponse;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
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
@SuppressWarnings({"PMD.ExcessiveImports"})
class UserDeletionAuditServiceTest {
    private static final String DEFAULT_SIZE = "10000";
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
            "", "", "", "", "", "", DEFAULT_SIZE, "1");

        final UserDeletionGetResponse response = userDeletionAuditService.getUserDeletions(params);

        PageRequest pageable = PageRequest.of(
            0, parseInt(DEFAULT_SIZE), Sort.by(Sort.Direction.DESC, "deletion_timestamp"));
        verify(userDeletionAuditFindRepository, times(1))
            .findUserDeletion(params, null, pageable);
        assertThat(response.getDeletionLogs()).hasSize(1);
        DeletionLog returned = response.getDeletionLogs().get(0);
        assertEquals("1", returned.getUserId(), "UserId mismatch");
        assertEquals("email", returned.getEmailAddress(), "email mismatch");
        assertEquals(1, response.getTotalNumberOfRecords(), "Expected total responses mismatch");
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

        final  List<DeletionLog> response =
            userDeletionAuditService.saveUserDeletion(Arrays.asList(deletionLog, deletionLog));

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

        final List<DeletionLog> response = userDeletionAuditService.saveUserDeletion(List.of(deletionLog));
        verify(userDeletionAuditRepository, times(1)).save(any());
        assertThat(response).hasSize(1);
        assertEquals("456", response.getFirst().getUserId(), "User ids are not equal");
        assertEquals("email@example.net", response.getFirst().getEmailAddress(), "Emails are not equal");
        assertEquals("First Name", response.getFirst().getFirstName(), "First names are not equal");
        assertEquals("Last Name", response.getFirst().getLastName(), "Last names are not equal");
        String timestampStr = new TimestampUtil().timestampConvertor(timestamp);
        assertEquals(timestampStr, response.getFirst().getDeletionTimestamp(), "Timestamps are not equal");
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

    private List<UserDeletionAudit> getAllUserDeletionAudit() {
        UserDeletionAudit firstUser = UserDeletionAudit.builder()
            .userId("1")
            .emailAddress("email1")
            .firstName("John")
            .lastName("Smith")
            .timestamp(Timestamp.from(Instant.now()))
            .build();
        UserDeletionAudit secondUser = UserDeletionAudit.builder()
            .userId("2")
            .emailAddress("email2")
            .firstName("Matt")
            .lastName("Scott")
            .timestamp(Timestamp.from(Instant.now()))
            .build();
        List<UserDeletionAudit> allUsers = new ArrayList<>();
        allUsers.add(firstUser);
        allUsers.add(secondUser);
        return allUsers;
    }

    @Test
    void shouldGetAllUsersDeletionAuditRepositoryResponse() {
        final List<UserDeletionAudit> allUsers = getAllUserDeletionAudit();
        final Page<UserDeletionAudit> results = new PageImpl<>(allUsers);
        when(userDeletionAuditFindRepository.findAllDeletedUsers(any(),any(),any()))
            .thenReturn(results);
        final DeletionLogAllUsersRequestParams params = new DeletionLogAllUsersRequestParams("1", DEFAULT_SIZE,  "");
        final UserDeletionGetResponse response = userDeletionAuditService.getAllDeletedUsers(params);

        Pageable pageable = userDeletionAuditService.getPageSorted("1", DEFAULT_SIZE, "desc");
        verify(userDeletionAuditFindRepository, times(1))
            .findAllDeletedUsers("DESC",null, pageable);
        assertThat(response.getDeletionLogs()).hasSize(2);
        DeletionLog returned = response.getDeletionLogs().getFirst();
        assertEquals("1", returned.getUserId(), "UserId mismatch");
        assertEquals("email1", returned.getEmailAddress(), "email mismatch");
        assertEquals("John", returned.getFirstName(), "firstName mismatch");
        assertEquals("Smith", returned.getLastName(), "lastName mismatch");
        returned = response.getDeletionLogs().get(1);
        assertEquals("2", returned.getUserId(), "UserId mismatch");
        assertEquals("email2", returned.getEmailAddress(), "email mismatch");
        assertEquals("Matt", returned.getFirstName(), "firstName mismatch");
        assertEquals("Scott", returned.getLastName(), "lastName mismatch");
        assertEquals(2, response.getTotalNumberOfRecords(), "Expected total responses mismatch");
    }

    @Test
    void shouldSortInDescIfSortIsNull() {
        Pageable pageable = userDeletionAuditService.getPageSorted("1", DEFAULT_SIZE, null);
        Sort sort = pageable.getSort();
        for (Sort.Order order : sort) {
            assertEquals(Sort.Direction.DESC,order.getDirection(),"Sort should be DESC");
        }
    }

    @Test
    void shouldSortInDesc() {
        Pageable pageable = userDeletionAuditService.getPageSorted("1", DEFAULT_SIZE, "DESC");
        Sort sort = pageable.getSort();
        for (Sort.Order order : sort) {
            assertEquals(Sort.Direction.DESC,order.getDirection(),"Sort should be DESC");
        }
    }

    @Test
    void shouldSortInAsc() {
        Pageable pageable = userDeletionAuditService.getPageSorted("1", DEFAULT_SIZE, "asc");
        Sort sort = pageable.getSort();
        for (Sort.Order order : sort) {
            assertEquals(Sort.Direction.ASC,order.getDirection(),"Sort should be Asc");
        }
    }
}
