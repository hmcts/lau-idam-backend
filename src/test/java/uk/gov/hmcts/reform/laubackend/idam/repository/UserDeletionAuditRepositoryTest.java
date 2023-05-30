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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.laubackend.idam.domain.UserDeletionAudit;
import uk.gov.hmcts.reform.laubackend.idam.dto.UserDeletionUser;
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
@SuppressWarnings("PMD.TooManyMethods")
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
        final var userDeletionAuditInsertRepository = new UserDeletionAuditInsertRepository(entityManager);
        //Insert ${RECORD_NUMBER} records
        for (int i = 1; i < RECORD_NUMBER + 1; i++) {
            userDeletionAuditInsertRepository
                .saveUserDeleteAuditWithEncryption(getUserDeletionAudit(
                    String.valueOf(i),
                    String.valueOf(i),
                    "first name " + i,
                    "last name " + i,
                    valueOf(now().plusDays(i))
                ), ENCRYPTION_KEY);
        }
        userDeletionAuditFindRepository = new UserDeletionAuditFindRepository(entityManager);
    }

    @Test
    void shouldSearchByUserId() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(getUserDeletionUser("1", null, null, null),
                              valueOf(now()),
                              valueOf(now().plusDays(20)),
                              ENCRYPTION_KEY,
                              getPage());

        assertThat(userDeletion.getContent()).hasSize(1);
        assertResults(userDeletion.getContent(), 1);
    }

    @Test
    @SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
    void shouldReturnResultsInDescOrderByTimestamp() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(getUserDeletionUser(null, null, null, null),
                              valueOf(now()),
                              valueOf(now().plusDays(20)),
                              ENCRYPTION_KEY,
                              getPage());

        assertThat(userDeletion.getContent()).hasSize(20);
        assertThat(userDeletion.getContent())
            .extracting("timestamp", Timestamp.class)
            .isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void shouldSearchByEmail() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(getUserDeletionUser(null, "1", null, null),
                              valueOf(now()),
                              valueOf(now().plusDays(20)),
                              ENCRYPTION_KEY,
                              getPage());

        assertThat(userDeletion.getContent()).hasSize(1);
        assertResults(userDeletion.getContent(), 1);
    }

    @Test
    void shouldSearchByFirstName() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(getUserDeletionUser(null, null, "first name 3", null),
                              valueOf(now()),
                              valueOf(now().plusDays(20)),
                              ENCRYPTION_KEY,
                              getPage());

        assertThat(userDeletion.getContent()).hasSize(1);
        assertResults(userDeletion.getContent(), 3);
    }

    @Test
    void shouldSearchByLastName() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(getUserDeletionUser(null, null, null, "last name 5"),
                              valueOf(now()),
                              valueOf(now().plusDays(20)),
                              ENCRYPTION_KEY,
                              getPage());

        assertThat(userDeletion.getContent()).hasSize(1);
        assertResults(userDeletion.getContent(), 5);
    }

    @Test
    void shouldSearchByFirstAndLastName() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(getUserDeletionUser(null, null, "first name 2", "last name 2"),
                              valueOf(now()),
                              valueOf(now().plusDays(20)),
                              ENCRYPTION_KEY,
                              getPage());

        assertThat(userDeletion.getContent()).hasSize(1);
        assertResults(userDeletion.getContent(), 2);
    }

    @Test
    void shouldFindPageableResults() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository.findUserDeletion(
            null,
            valueOf(now()),
            valueOf(now().plusDays(20)),
            ENCRYPTION_KEY,
            PageRequest.of(1, 10)
        );

        assertThat(userDeletion.getTotalElements()).isEqualTo(20);
        assertThat(userDeletion.getContent()).hasSize(10);
    }

    @Test
    void shouldDeleteUserDeletionRecord() {
        final var userDeletionAuditInsertRepository = new UserDeletionAuditInsertRepository(entityManager);
        UserDeletionAudit user = getUserDeletionAudit("userId",
                                                      "email@example.net",
                                                      "First",
                                                      "Last",
                                                      valueOf(now()));
        userDeletionAuditInsertRepository.saveUserDeleteAuditWithEncryption(user, ENCRYPTION_KEY);

        Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository.findUserDeletion(
            getUserDeletionUser(user.getUserId(), null, null, null),
            valueOf(now().minusDays(1)),
            valueOf(now().plusDays(30)),
            ENCRYPTION_KEY,
            getPage()
        );

        assertThat(userDeletion.getContent()).hasSize(1);
        assertThat(userDeletion.getContent().get(0).getUserId()).isEqualTo(user.getUserId());

        userDeletionAuditRepository.deleteById(userDeletion.getContent().get(0).getId());

        userDeletion = userDeletionAuditFindRepository.findUserDeletion(
            getUserDeletionUser(user.getUserId(), null, null, null),
            valueOf(now()),
            valueOf(now().plusDays(10)),
            ENCRYPTION_KEY,
            getPage()
        );

        assertThat(userDeletion.getContent()).isEmpty();
    }

    static void assertResults(final List<UserDeletionAudit> content, final int value) {
        final String stringValue = String.valueOf(value);
        assertThat(content.get(0).getUserId()).isEqualTo(stringValue);
        assertThat(content.get(0).getEmailAddress()).isEqualTo(stringValue);
        assertThat(content.get(0).getFirstName()).isEqualTo("first name " + value);
        assertThat(content.get(0).getLastName()).isEqualTo("last name " + value);
    }

    static UserDeletionAudit getUserDeletionAudit(final String userId,
                                                   final String emailAddress,
                                                   final String firstName,
                                                   final String lastName,
                                                   final Timestamp timestamp) {
        final UserDeletionAudit userDeletionAudit = new UserDeletionAudit();
        userDeletionAudit.setUserId(userId);
        userDeletionAudit.setEmailAddress(emailAddress);
        userDeletionAudit.setFirstName(firstName);
        userDeletionAudit.setLastName(lastName);
        userDeletionAudit.setTimestamp(timestamp);
        return userDeletionAudit;
    }

    static UserDeletionUser getUserDeletionUser(final String userId,
                                                 final String emailAddress,
                                                 final String firstName,
                                                 final String lastName) {
        return new UserDeletionUser(userId, emailAddress, firstName, lastName);
    }

    static Pageable getPage() {
        return PageRequest.of(0, 100);
    }
}
