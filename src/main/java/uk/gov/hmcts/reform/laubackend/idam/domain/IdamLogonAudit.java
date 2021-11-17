package uk.gov.hmcts.reform.laubackend.idam.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

import static javax.persistence.GenerationType.IDENTITY;


@Entity(name = "idam_logon_audit")
@NoArgsConstructor
@Getter
@Setter
public class IdamLogonAudit implements Serializable {

    public static final long serialVersionUID = 5428747L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "service", nullable = false)
    private String service;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "log_timestamp", nullable = false)
    private Timestamp timestamp;
}
