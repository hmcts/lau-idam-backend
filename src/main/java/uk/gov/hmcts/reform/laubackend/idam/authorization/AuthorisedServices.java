package uk.gov.hmcts.reform.laubackend.idam.authorization;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorisedServices {

    @Value("#{'${authorised.services}'.split(',\\s*')}")
    private List<String> authorisedServicesList;

    public boolean hasService(String serviceName) {
        return authorisedServicesList.contains(serviceName);
    }
}
