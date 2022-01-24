Feature: The application's DELETE logon endpoint

  Scenario: The backend is able to process logon DELETE requests
    Given LAU IdAm backend application is healthy
    When I request POST "/audit/logon" logon endpoint using userId "77777"
    And I request DELETE "/audit/logon/deleteAuditLogonRecord" logon endpoint
    And I GET "/audit/logon" logon using userId "77777"
    Then An empty result for DELETE logon is returned

  Scenario: The backend is unable to process logon DELETE requests due to missing s2s header
    Given LAU IdAm backend application is healthy
    And I request DELETE "/audit/logon/deleteAuditLogonRecord/" logon endpoint with missing s2s header
    Then http "403" response is returned for DELETE logon

  Scenario: The backend is unable to process logon DELETE requests due to invalid authorization header
    Given LAU IdAm backend application is healthy
    And I request DELETE "/audit/logon/deleteAuditLogonRecord/" logon endpoint with missing authorization header
    Then http "401" response is returned for DELETE logon

  Scenario: The backend is unable to process logon DELETE requests due invalid case search id
    Given LAU IdAm backend application is healthy
    And I request DELETE "/audit/logon/deleteAuditLogonRecord/" logon endpoint with invalid logonId "000000"
    Then http "404" response is returned for DELETE logon

  Scenario: The backend is unable to process logon DELETE requests due invalid case search id
    Given LAU IdAm backend application is healthy
    And I request DELETE "/audit/logon/deleteAuditLogonRecord/" logon endpoint with missing logonId
    Then http "400" response is returned for DELETE logon



