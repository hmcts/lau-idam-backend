package uk.gov.hmcts.reform.laubackend.idam.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.sql.Timestamp;
import java.util.List;

import static java.sql.Timestamp.valueOf;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.PageRequest.of;

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
class IdamLogonRepositoryStartEndTimeTest {

    private static final String ENCRYPTION_KEY = "ThisIsATestKeyForEncryption";
    private static final int NUMBER_OF_ENTRIES = 20;

    @PersistenceContext
    private EntityManager entityManager;

    private IdamLogonAuditFindLogonRepository idamLogonAuditFindLogonRepository;

    @BeforeEach
    public void setUp() {
        final IdamLogonAuditInsertRepository idamLogonAuditInsertRepository =
                new IdamLogonAuditInsertRepository(entityManager);
        //Insert ${NUMBER_OF_ENTRIES} records
        for (int i = 1; i < NUMBER_OF_ENTRIES + 1; i++) {
            idamLogonAuditInsertRepository
                    .saveIdamLogonAuditWithEncryption(getIdamLogonAudit(
                            String.valueOf(i),
                            String.valueOf(i),
                            valueOf(now().plusDays(i))
                    ), ENCRYPTION_KEY);
        }

        idamLogonAuditFindLogonRepository = new IdamLogonAuditFindLogonRepository(entityManager);
    }

    @Test
    void shouldFindIdamLogonStartTimeAndEndTime1() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditFindLogonRepository.findIdamLogon(
                null,
                null,
                valueOf(now().plusDays(10)),
                valueOf(now().plusDays(20)),
                ENCRYPTION_KEY,
                getPage()
        );
        //Will return 10 days because  the date start is +10 from now
        assertThat(idamLogon.getContent()).hasSize(10);
    }

    @Test
    void shouldFindIdamLogonByStartTimeEndTime2() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditFindLogonRepository.findIdamLogon(
                "1",
                null,
                valueOf(now()),
                valueOf(now().plusDays(1)),
                ENCRYPTION_KEY,
                getPage()
        );
        assertThat(idamLogon.getContent()).hasSize(1);
        assertResults(idamLogon.getContent(), 1);
    }

    @Test
    void shouldNotFindIdamLogonStartTimeAndEndTime() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditFindLogonRepository.findIdamLogon(
                null,
                null,
                valueOf(now().plusDays(20)),
                null,
                ENCRYPTION_KEY,
                getPage()
        );
        assertThat(idamLogon.getContent()).isEmpty();
    }

    @Test
    void shouldNotFindIdamLogonEndTime() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditFindLogonRepository.findIdamLogon(
                null,
                null,
                null,
                valueOf(now().minusDays(1)),
                ENCRYPTION_KEY,
                getPage()
        );
        assertThat(idamLogon.getContent()).isEmpty();
    }

    @Test
    void shouldNotFindIdamLogonByStartTimeEndTime() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditFindLogonRepository.findIdamLogon(
                null,
                null,
                valueOf(now().minusDays(1)),
                valueOf(now().minusDays(1)),
                ENCRYPTION_KEY,
                getPage()
        );
        assertThat(idamLogon.getContent()).isEmpty();
    }

    @Test
    void shouldFindAllIdamLogonsWithoutStartTimeAndEndTime() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditFindLogonRepository.findIdamLogon(
            null,
            null,
            null,
            null,
            ENCRYPTION_KEY,
            getPage()
        );
        assertThat(idamLogon.getContent()).hasSize(NUMBER_OF_ENTRIES);
    }


    private void assertResults(final List<IdamLogonAudit> content, final int value) {
        final String stringValue = String.valueOf(value);
        assertThat(content.get(0).getUserId()).isEqualTo(stringValue);
        assertThat(content.get(0).getEmailAddress()).isEqualTo(stringValue);
        assertThat(content.get(0).getIpAddress()).isEqualTo("a.b.c.d");
        assertThat(content.get(0).getService()).isEqualTo("service");
    }

    private IdamLogonAudit getIdamLogonAudit(final String emailAddress,
                                             final String userId,
                                             final Timestamp timestamp) {
        final IdamLogonAudit idamLogonAudit = new IdamLogonAudit();
        idamLogonAudit.setEmailAddress(emailAddress);
        idamLogonAudit.setService("service");
        idamLogonAudit.setUserId(userId);
        idamLogonAudit.setTimestamp(timestamp);
        idamLogonAudit.setIpAddress("a.b.c.d");
        return idamLogonAudit;
    }

    private Pageable getPage() {
        return of(0, 100);
    }
}
