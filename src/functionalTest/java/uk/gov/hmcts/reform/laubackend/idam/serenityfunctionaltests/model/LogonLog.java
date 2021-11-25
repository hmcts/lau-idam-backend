package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("jsonschema2pojo")
public class LogonLog {

    @JsonProperty("id")
    private String id;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("emailAddress")
    private String emailAddress;
    @JsonProperty("service")
    private String service;
    @JsonProperty("ipAddress")
    private String ipAddress;
    @JsonProperty("timestamp")
    private String timestamp;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getEmailAddress() {
        return emailAddress;
    }


    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


    public String getService() {
        return service;
    }


    public void setService(String service) {
        this.service = service;
    }


    public String getIpAddress() {
        return ipAddress;
    }


    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    public String getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
