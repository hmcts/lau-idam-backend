package uk.gov.hmcts.reform.laubackend.idam.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

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

    @ColumnTransformer(
            read = "pgp_sym_decrypt(decode(email_address, 'base64'), '${encryption.key}')",
            write = "encode(pgp_sym_encrypt(?, '${encryption.key}'), 'base64')"
    )
    @Column(name = "email_address", nullable = false, columnDefinition = "bytea")
    private String emailAddress;

    @Column(name = "service", nullable = false)
    private String service;

    @ColumnTransformer(
            read = "pgp_sym_decrypt(decode(ip_address, 'base64'), '${encryption.key}')",
            write = "encode(pgp_sym_encrypt(?, '${encryption.key}'), 'base64')"
    )
    @Column(name = "ip_address", nullable = true, columnDefinition = "bytea")
    private String ipAddress;

    @Column(name = "log_timestamp", nullable = false)
    private Timestamp timestamp;
}
