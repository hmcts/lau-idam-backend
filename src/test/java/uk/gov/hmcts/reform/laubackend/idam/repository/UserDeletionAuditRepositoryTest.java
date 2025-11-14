package uk.gov.hmcts.reform.laubackend.idam.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.laubackend.idam.domain.UserDeletionAudit;
import uk.gov.hmcts.reform.laubackend.idam.dto.DeletionLogGetRequestParams;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

import static java.sql.Timestamp.valueOf;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=update",
    "spring.liquibase.enabled=false",
    "spring.flyway.enabled=true"
})
@Import({TimestampUtil.class})
@SuppressWarnings({"PMD.TooManyMethods"})
class UserDeletionAuditRepositoryTest {
    private static final int RECORD_NUMBER = 20;

    private static final String ENCRYPTION_KEY = "ThisIsATestKeyForEncryption";

    @Autowired
    private UserDeletionAuditRepository userDeletionAuditRepository;
    private UserDeletionAuditFindRepository userDeletionAuditFindRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        final UserDeletionAuditInsertRepository userDeletionAuditInsertRepository =
            new UserDeletionAuditInsertRepository(entityManager);
        //Insert ${RECORD_NUMBER} records
        for (int i = 1; i < RECORD_NUMBER + 1; i++) {
            userDeletionAuditInsertRepository
                .saveUserDeleteAuditWithEncryption(getUserDeletionAudit(
                    String.valueOf(i),
                    "Email" + i + "@Example.ORG  ",
                    "   First " + i + " Name ",
                    "  Last " + i + " Name  ",
                    valueOf(now().plusDays(i))
                ), ENCRYPTION_KEY);
        }

        userDeletionAuditFindRepository = new UserDeletionAuditFindRepository(new TimestampUtil(), entityManager);
    }

    @Test
    void shouldSearchByUserId() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(
                getRequestParams("1", null, null, null, now().toString(), now().plusDays(20).toString()),
                ENCRYPTION_KEY,
                getPage()
            );

        assertThat(userDeletion.getContent()).hasSize(1);
        assertResults(userDeletion.getContent(), 1);
    }

    @Test
    void shouldReturnResultsInDescOrderByTimestamp() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(
                getRequestParams(
                    null,
                    null,
                    null,
                    null,
                    now().toString(),
                    now().plusDays(20).toString()
                ),
                ENCRYPTION_KEY,
                getPage()
            );

        assertThat(userDeletion.getContent()).hasSize(20);
        assertThat(userDeletion.getContent())
            .extracting("timestamp", Timestamp.class)
            .isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void shouldSearchByEmail() {
        String email = "  EMAIL1@EXAMPLE.ORG  ";
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(
                getRequestParams(null, email, null, null, now().toString(), now().plusDays(20).toString()),
                ENCRYPTION_KEY,
                getPage());

        assertThat(userDeletion.getContent()).hasSize(1);
        assertResults(userDeletion.getContent(), 1);
    }

    @Test
    void shouldSearchByFirstName() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(
                getRequestParams(null, null, " first 3 name ", null, now().toString(), now().plusDays(20).toString()),
                ENCRYPTION_KEY,
                getPage());

        assertThat(userDeletion.getContent()).hasSize(1);
        assertResults(userDeletion.getContent(), 3);
    }

    @Test
    void shouldSearchByLastName() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(
                getRequestParams(null, null, null, "LAST 5 NAME", now().toString(), now().plusDays(20).toString()),
                ENCRYPTION_KEY,
                getPage());

        assertThat(userDeletion.getContent()).hasSize(1);
        assertResults(userDeletion.getContent(), 5);
    }

    @Test
    void shouldSearchByFirstAndLastName() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(
                getRequestParams(
                    null,
                    null,
                    "First 2 Name",
                    "  Last 2 Name",
                    now().toString(),
                    now().plusDays(20).toString()
                ),
                ENCRYPTION_KEY,
                getPage());

        assertThat(userDeletion.getContent()).hasSize(1);
        assertResults(userDeletion.getContent(), 2);
    }

    @Test
    void shouldFindPageableResults() {
        final DeletionLogGetRequestParams params = new DeletionLogGetRequestParams(
            null,
            null,
            null,
            null,
            now().toString(),
            now().plusDays(21).toString(),
            "",
            ""
        );
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository.findUserDeletion(
            params,
            ENCRYPTION_KEY,
            PageRequest.of(1, 10)
        );

        assertThat(userDeletion.getTotalElements()).isEqualTo(20);
        assertThat(userDeletion.getContent()).hasSize(10);
    }

    @Test
    void shouldDeleteUserDeletionRecord() {
        final UserDeletionAuditInsertRepository userDeletionAuditInsertRepository =
            new UserDeletionAuditInsertRepository(entityManager);
        UserDeletionAudit user = getUserDeletionAudit("userId",
                                                      "EMAIL@EXAMPLE.NET",
                                                      "First",
                                                      "Last",
                                                      valueOf(now()));
        userDeletionAuditInsertRepository.saveUserDeleteAuditWithEncryption(user, ENCRYPTION_KEY);

        Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository.findUserDeletion(
            getRequestParams(
                user.getUserId(),
                null,
                null,
                null,
                now().minusDays(1).toString(),
                now().plusDays(30).toString()
            ),
            ENCRYPTION_KEY,
            getPage()
        );

        assertThat(userDeletion.getContent()).hasSize(1);
        assertThat(userDeletion.getContent().getFirst().getUserId()).isEqualTo(user.getUserId());

        userDeletionAuditRepository.deleteById(userDeletion.getContent().getFirst().getId());

        userDeletion = userDeletionAuditFindRepository.findUserDeletion(
            getRequestParams(user.getUserId(), null, null, null, now().toString(), now().plusDays(10).toString()),
            ENCRYPTION_KEY,
            getPage()
        );

        assertThat(userDeletion.getContent()).isEmpty();
    }

    static void assertResults(final List<UserDeletionAudit> content, final int value) {
        final String stringValue = String.valueOf(value);
        assertThat(content.getFirst().getUserId()).isEqualTo(stringValue);
        assertThat(content.getFirst().getEmailAddress()).isEqualTo("Email" + stringValue + "@Example.ORG");
        assertThat(content.getFirst().getFirstName()).isEqualTo("First " + value + " Name");
        assertThat(content.getFirst().getLastName()).isEqualTo("Last " + value + " Name");
    }

    static UserDeletionAudit getUserDeletionAudit(
        final String userId,
        final String emailAddress,
        final String firstName,
        final String lastName,
        final Timestamp timestamp
    ) {
        final UserDeletionAudit userDeletionAudit = new UserDeletionAudit();
        userDeletionAudit.setUserId(userId);
        userDeletionAudit.setEmailAddress(emailAddress);
        userDeletionAudit.setFirstName(firstName);
        userDeletionAudit.setLastName(lastName);
        userDeletionAudit.setTimestamp(timestamp);
        return userDeletionAudit;
    }

    static DeletionLogGetRequestParams getRequestParams(
        final String userId,
        final String emailAddress,
        final String firstName,
        final String lastName,
        final String startTime,
        final String endTime
    ) {

        return new DeletionLogGetRequestParams(
            userId,
            emailAddress,
            firstName,
            lastName,
            startTime,
            endTime,
            "100",
            "1"
        );
    }

    static Pageable getPage() {
        return PageRequest.of(0, 100);
    }

    @Test
    void shouldFindPageableResultsForAllUsers() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository.findAllDeletedUsers(
            "ASC",
            ENCRYPTION_KEY,
            PageRequest.of(0,10, Sort.Direction.ASC,"deletion_timestamp")
        );

        assertThat(userDeletion.getTotalElements()).isEqualTo(20);
        assertThat(userDeletion.getContent()).hasSize(10);
        assertResults(userDeletion.getContent(), 1);
    }

    @Test
    void shouldFindResultsForAllUsers() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository.findAllDeletedUsers(
            "DESC",
            ENCRYPTION_KEY,
            PageRequest.of(0,10_000,Sort.Direction.DESC,"deletion_timestamp")
        );

        assertThat(userDeletion.getTotalElements()).isEqualTo(20);
        assertThat(userDeletion.getContent()).hasSize(20);
        assertResults(userDeletion.getContent(), 20);
    }
}
