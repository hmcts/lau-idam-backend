package uk.gov.hmcts.reform.laubackend.idam.bdd;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.Getter;

@Getter
public enum WiremokInstantiator {
    INSTANCE;

    private final WireMockServer wireMockServer = new WireMockServer(4554);

    WiremokInstantiator() {
        wireMockServer.start();
    }
}
