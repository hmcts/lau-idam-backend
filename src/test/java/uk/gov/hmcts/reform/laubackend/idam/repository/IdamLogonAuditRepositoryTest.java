package uk.gov.hmcts.reform.laubackend.idam.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.laubackend.idam.domain.IdamLogonAudit;

import java.sql.Timestamp;
import java.util.List;

import static java.sql.Timestamp.valueOf;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false",
        "spring.flyway.enabled=true"
})
class IdamLogonAuditRepositoryTest {

    @Autowired
    private IdamLogonAuditRepository idamLogonAuditRepository;

    @Test
    void shouldSaveIdamLogonAudit() {

        final Timestamp timestamp = valueOf(now());

        final IdamLogonAudit idamLogonAudit = getIdamLogonAudit(timestamp);
        idamLogonAuditRepository.save(idamLogonAudit);

        final List<IdamLogonAudit> idamLogonAuditList = idamLogonAuditRepository.findAll();

        assertThat(idamLogonAuditList.size()).isEqualTo(1);
        assertThat(idamLogonAuditList.get(0).getId()).isNotNull();
        assertThat(idamLogonAuditList.get(0).getEmailAddress()).isEqualTo("some.random.mail@supercoolmail.com");
        assertThat(idamLogonAuditList.get(0).getIpAddress()).isEqualTo("random.ip.address");
        assertThat(idamLogonAuditList.get(0).getUserId()).isEqualTo("123");
        assertThat(idamLogonAuditList.get(0).getService()).isEqualTo("service");
        assertThat(idamLogonAuditList.get(0).getTimestamp()).isEqualTo(timestamp);
    }

    private IdamLogonAudit getIdamLogonAudit(final Timestamp timestamp) {
        final IdamLogonAudit idamLogonAudit = new IdamLogonAudit();
        idamLogonAudit.setEmailAddress("some.random.mail@supercoolmail.com");
        idamLogonAudit.setIpAddress("random.ip.address");
        idamLogonAudit.setUserId("123");
        idamLogonAudit.setService("service");
        idamLogonAudit.setTimestamp(timestamp);

        return idamLogonAudit;
    }
}