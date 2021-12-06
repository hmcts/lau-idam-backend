package uk.gov.hmcts.reform.laubackend.idam.authorization;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class AuthorisedServices {

    @Value("#{'${authorised.services}'.split(',\\s*')}")
    private List<String> authorisedServicesList;

    @Value("#{'${authorised.roles}'.split(',\\s*')}")
    private List<String> authorisedRolesList;

    public boolean hasService(String serviceName) {
        return authorisedServicesList.contains(serviceName);
    }

    public boolean hasRole(final List<String> roleNameList) {
        return CollectionUtils.containsAny(roleNameList, authorisedRolesList);
    }
}
