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

  Scenario: The backend is able to process 10 different idam logons POST requests in 10 different threads
    Given LAU IdAm backend application is healthy
    When I POST 10 request to "/audit/logon" endpoint with s2s in asynchronous mode
    Then  logon response body is returned for all ten requests

  Scenario: The backend is able to process idam logons POST requests with failure
    Given LAU IdAm backend application is healthy
    When I POST a request to "/audit/logon" endpoint with s2s with simulate failure
    Then   it should try making retry call for authorisation details

