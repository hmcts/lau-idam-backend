Feature: The application's POST logon audit endpoint

  Scenario: The backend is able to process IdAM logons POST requests
    Given LAU IdAm backend application is healthy
    When I POST IdAM login using "/audit/logon" endpoint using s2s
    Then  logon response body is returned

  Scenario: The backend is unable to to process IdAM logons POST requests due to missing mandatory parameter
    Given LAU IdAm backend application is healthy
    When I POST "/audit/logon" endpoint with missing request body parameter using s2s
    Then http bad request response is returned for POST logon

  Scenario: The backend is to process IdAM logons POST requests due to invalid parameter
    Given LAU IdAm backend application is healthy
    When I POST "/audit/logon" endpoint with invalid body parameter using s2s
    Then http bad request response is returned for POST logon

  Scenario: The backend is to process IdAM logons POST requests with invalid s2s
    Given LAU IdAm backend application is healthy
    When I POST "/audit/logon" endpoint with invalid s2s
    Then http forbidden response is returned for POST logon
    And it should try making retry call for authorisation details
