package uk.gov.hmcts.reform.laubackend.idam.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

import static jakarta.persistence.GenerationType.IDENTITY;


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

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "login_state")
    private String loginState;

    @Column(name = "log_timestamp", nullable = false)
    private Timestamp timestamp;
}
