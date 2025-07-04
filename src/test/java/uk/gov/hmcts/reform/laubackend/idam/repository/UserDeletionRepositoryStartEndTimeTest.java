package uk.gov.hmcts.reform.laubackend.idam.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.laubackend.idam.domain.UserDeletionAudit;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.sql.Timestamp;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.laubackend.idam.repository.UserDeletionAuditRepositoryTest.assertResults;
import static uk.gov.hmcts.reform.laubackend.idam.repository.UserDeletionAuditRepositoryTest.getPage;
import static uk.gov.hmcts.reform.laubackend.idam.repository.UserDeletionAuditRepositoryTest.getRequestParams;
import static uk.gov.hmcts.reform.laubackend.idam.repository.UserDeletionAuditRepositoryTest.getUserDeletionAudit;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=update",
    "spring.liquibase.enabled=false",
    "spring.flyway.enabled=true",
    "idam.s2s-auth.url=http://localhost:4502"
})
@Import({TimestampUtil.class})
class UserDeletionRepositoryStartEndTimeTest {
    private static final int RECORD_NUMBER = 20;

    private static final String ENCRYPTION_KEY = "ThisIsATestKeyForEncryption";

    private UserDeletionAuditFindRepository userDeletionAuditFindRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
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
                    Timestamp.valueOf(now().plusDays(i))
                ), ENCRYPTION_KEY);
        }

        userDeletionAuditFindRepository = new UserDeletionAuditFindRepository(new TimestampUtil(), entityManager);
    }


    @Test
    @DisplayName("Should find half by searching first half time range")
    void shouldFindHalfByTimeRange() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(
                getRequestParams(
                    null,
                    null,
                    null,
                    null,
                    now().toString(),
                    now().plusDays(10).toString()
                ),
                ENCRYPTION_KEY,
                getPage());

        assertThat(userDeletion.getContent()).hasSize(10);
    }

    @Test
    @DisplayName("Should find half by searching second half time range")
    void shouldFindOtherHalfByTimeRange() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(
                getRequestParams(
                    null, null, null, null,
                    now().plusDays(10).toString(),
                    now().plusDays(20).toString()
                ),
                ENCRYPTION_KEY,
                getPage());

        assertThat(userDeletion.getContent()).hasSize(10);
    }

    @Test
    @DisplayName("Should find all by time range")
    void shouldFindAllByTimeRange() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(
                getRequestParams(
                    null, null, null, null,
                    now().toString(),
                    now().plusDays(20).toString()
                ),
                ENCRYPTION_KEY,
                getPage());

        assertThat(userDeletion.getContent()).hasSize(RECORD_NUMBER);
    }

    @Test
    void shouldFindOneByTimeRange() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(
                getRequestParams(
                    null, null, null, null,
                    now().toString(),
                    now().plusDays(1).toString()
                ),
                ENCRYPTION_KEY,
                getPage());
        assertThat(userDeletion.getContent()).hasSize(1);
        assertResults(userDeletion.getContent(), 1);
    }

    @Test
    void shouldNotFindFromFutureByTimeRange() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(
                getRequestParams(
                    null, null, null, null,
                    now().plusDays(20).toString(),
                    now().plusDays(40).toString()
                ),
                ENCRYPTION_KEY,
                getPage());
        assertThat(userDeletion.getContent()).isEmpty();
    }

    @Test
    void shouldNotFindBeforeRecordsBeganByTimeRange() {
        final Page<UserDeletionAudit> userDeletion = userDeletionAuditFindRepository
            .findUserDeletion(
                getRequestParams(
                    null, null, null, null,
                    now().minusDays(10).toString(),
                    now().minusDays(1).toString()
                ),
                ENCRYPTION_KEY,
                getPage());

        assertThat(userDeletion.getContent()).isEmpty();
    }
}
