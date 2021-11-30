Feature: The application's GET audit logon endpoint

  Scenario: The backend is able to process userId logon audit GET requests
    Given LAU IdAm backend application is healthy
    When I POST multiple records to "/audit/logon" endpoint using "1,2,3" userIds
    And I GET "/audit/logon" using userId "1" query param
    Then a single logon response body is returned for userId "1"

