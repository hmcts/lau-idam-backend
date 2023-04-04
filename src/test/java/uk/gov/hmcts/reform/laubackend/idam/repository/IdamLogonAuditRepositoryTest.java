package uk.gov.hmcts.reform.laubackend.idam.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;
import uk.gov.hmcts.reform.laubackend.idam.utils.TimestampUtil;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
        "spring.flyway.enabled=true"
})
@Import({TimestampUtil.class})
@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
class IdamLogonAuditRepositoryTest {

    private static final String ENCRYPTION_KEY = "ThisIsATestKeyForEncryption";

    @Autowired
    private IdamLogonAuditRepository idamLogonAuditRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        final IdamLogonAuditInsertRepository idamLogonAuditInsertRepository =
                new IdamLogonAuditInsertRepository(entityManager);
        //Insert 20 records
        for (int i = 1; i < 21; i++) {
            idamLogonAuditInsertRepository
                    .saveIdamLogonAuditWithEncryption(getIdamLogonAudit(
                            String.valueOf(i),
                            String.valueOf(i),
                            valueOf(now().plusDays(i))
                    ), ENCRYPTION_KEY);
        }
    }

    @Test
    void shouldSearchByEmail() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditRepository
                .findIdamLogon(null, "1", valueOf(now()),
                               valueOf(now().plusDays(20)), ENCRYPTION_KEY, getPage());

        assertThat(idamLogon.getContent().size()).isEqualTo(1);
        assertResults(idamLogon.getContent(), 1);
    }

    @Test
    void shouldSearchUserId() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditRepository
                .findIdamLogon("1",
                        null,
                               valueOf(now()),
                               valueOf(now().plusDays(20)),
                        ENCRYPTION_KEY,
                        getPage());

        assertThat(idamLogon.getContent().size()).isEqualTo(1);
        assertResults(idamLogon.getContent(), 1);
    }

    @Test
    void shouldGetAllRecords() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditRepository
                .findIdamLogon(null,
                        null,
                         valueOf(now()),
                         valueOf(now().plusDays(20)),
                        ENCRYPTION_KEY,
                        getPage());

        assertThat(idamLogon.getContent().size()).isEqualTo(20);
    }

    @Test
    void shouldFindPageableResults() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditRepository.findIdamLogon(null,
                null,
                 valueOf(now()),
                 valueOf(now().plusDays(20)),
                ENCRYPTION_KEY,
                of(1, 10, Sort.by("log_timestamp"))
        );

        assertThat(idamLogon.getTotalElements()).isEqualTo(20);
        assertThat(idamLogon.getContent().size()).isEqualTo(10);
    }

    @Test
    void shouldDeleteIdamLogonRecord() {
        final IdamLogonAuditInsertRepository idamLogonAuditInsertRepository =
                new IdamLogonAuditInsertRepository(entityManager);
        idamLogonAuditInsertRepository
                .saveIdamLogonAuditWithEncryption(getIdamLogonAudit(
                        "test@super-tester.com",
                        "77777",
                        valueOf(now())
                ),ENCRYPTION_KEY);
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditRepository
                .findIdamLogon("77777",
                        null,
                               valueOf(now().minusDays(1)),
                               valueOf(now().plusDays(1)),
                        ENCRYPTION_KEY,
                        getPage());

        assertThat(idamLogon.getContent().size()).isEqualTo(1);
        assertThat(idamLogon.getContent().get(0).getUserId()).isEqualTo("77777");

        idamLogonAuditRepository.deleteById(idamLogon.getContent().get(0).getId());

        final Page<IdamLogonAudit> idamLogon1 = idamLogonAuditRepository
                .findIdamLogon("77777",
                        null,
                        null,
                        null,
                        ENCRYPTION_KEY,
                        getPage());

        assertThat(idamLogon1.getContent().size()).isEqualTo(0);
    }

    private void assertResults(final List<IdamLogonAudit> content, final int value) {
        final String stringValue = String.valueOf(value);
        assertThat(content.get(0).getUserId()).isEqualTo(stringValue);
        assertThat(content.get(0).getEmailAddress()).isEqualTo(stringValue);
        assertThat(content.get(0).getIpAddress()).isEqualTo("a.b.c.d");
        assertThat(content.get(0).getService()).isEqualTo("service");
        assertThat(content.get(0).getLoginState()).isEqualTo("AUTHENTICATE");
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
        idamLogonAudit.setLoginState("AUTHENTICATE");
        return idamLogonAudit;
    }

    private Pageable getPage() {
        return of(0, 100);
    }
}
