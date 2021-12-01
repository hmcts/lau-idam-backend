package uk.gov.hmcts.reform.laubackend.idam.config;

import org.hibernate.annotations.ColumnTransformer;

@SuppressWarnings("PMD.UnusedPrivateField")
public class TestIdamLogonAudit {

    @ColumnTransformer(
        read =  "test(decode(email_address, 'base64'), '${encryption.key}')",
        write = "encode(test(?, '${encryption.key}'), 'base64')"
    )
    private String emailAddress;

    @ColumnTransformer(
        read =  "test(decode(ip_address, 'base64'), '${encryption.key}')",
        write = "encode(test(?, '${encryption.key}'), 'base64')"
    )
    private String ipAddress;

}
