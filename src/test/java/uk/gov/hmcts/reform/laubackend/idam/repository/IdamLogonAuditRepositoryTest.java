package uk.gov.hmcts.reform.laubackend.idam.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

import java.sql.Timestamp;
import java.util.List;

import static java.sql.Timestamp.valueOf;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.liquibase.enabled=false",
        "spring.flyway.enabled=true"
})
@Import({RemoveColumnTransformers.class})
class IdamLogonAuditRepositoryTest {

    @Autowired
    private IdamLogonAuditRepository idamLogonAuditRepository;

    @BeforeEach
    public void setUp() {
        //Insert 20 records
        for (int i = 1; i < 21; i++) {
            idamLogonAuditRepository
                    .save(getIdamLogonAudit(
                            String.valueOf(i),
                            String.valueOf(i),
                            valueOf(now().plusDays(i))
                    ));
        }
    }

    @Test
    void shouldSearchByEmail() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditRepository
                .findIdamLogon(null, "1", null, null, null);

        assertThat(idamLogon.getContent().size()).isEqualTo(1);
        assertResults(idamLogon.getContent(), 1);
    }

    @Test
    void shouldSearchUserId() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditRepository
                .findIdamLogon("1",
                        null,
                        null,
                        null,
                        null);

        assertThat(idamLogon.getContent().size()).isEqualTo(1);
        assertResults(idamLogon.getContent(), 1);
    }

    @Test
    void shouldGetAllRecords() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditRepository
                .findIdamLogon(null,
                        null,
                        null,
                        null,
                        null);

        assertThat(idamLogon.getContent().size()).isEqualTo(20);
    }

    @Test
    void shouldFindPageableResults() {
        final Page<IdamLogonAudit> idamLogon = idamLogonAuditRepository.findIdamLogon(null,
                null,
                null,
                null,
                PageRequest.of(1, 10, Sort.by("timestamp"))
        );
        assertThat(idamLogon.getTotalElements()).isEqualTo(20);
        assertThat(idamLogon.getContent().size()).isEqualTo(10);
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
}