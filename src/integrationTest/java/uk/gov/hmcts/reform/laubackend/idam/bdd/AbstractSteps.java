package uk.gov.hmcts.reform.laubackend.idam.bdd;

import com.google.gson.Gson;
import org.springframework.boot.web.server.LocalServerPort;

public class AbstractSteps {

    public final Gson jsonReader = new Gson();

    @LocalServerPort
    private int port;

    public String baseUrl() {
        return "http://localhost:" + port;
    }
}
