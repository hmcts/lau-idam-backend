Feature: The application's GET audit logon endpoint

  Scenario: The backend is able to process userId logon GET requests
    Given LAU IdAm backend application is healthy
    When I POST multiple records to "/audit/logon" endpoint using "1,2,3" userIds
    And And I GET "/audit/logon" using userId "1" query param
    Then a single logon response body is returned for userId "1"

  Scenario: The backend is able to process emailAddress logon GET requests
    Given LAU IdAm backend application is healthy
    When I POST multiple records to "/audit/logon" endpoint using "test1@email.com,test2@email.com,test3@email.com" emailAddresses
    And And I GET "/audit/logon" using emailAddress "test2@email.com" query param
    Then a single logon response body is returned for emailAddress "test2@email.com"

  Scenario: The backend is able to process startTime endTime logon GET requests
    Given LAU IdAm backend application is healthy
    When I POST multiple records to "/audit/logon" endpoint using "2021-08-23T22:20:05.023Z,2022-08-23T22:20:05.023Z,2023-08-23T22:20:05.023Z" timestamp
    And And I GET "/audit/logon" using startTimestamp "2023-08-22T22:20:05" endTimestamp "2023-08-25T22:20:05" userId "1" query param
    Then a single logon response body is returned for startTimestamp "2023-08-23T22:20:05.023Z"

  Scenario: The backend is unable to process logon GET requests due to missing s2s
    Given LAU IdAm backend application is healthy
    When And I GET "/audit/logon" without service authorization header
    Then HTTP "403" Forbidden response is returned

  Scenario: The backend is unable to process logon GET requests due to missing authorization
    Given LAU IdAm backend application is healthy
    When And I GET "/audit/logon" without authorization header
    Then HTTP "401" Unauthorized response is returned


  Scenario: The backend is unable to process logon GET requests due to missing search params
    Given LAU IdAm backend application is healthy
    When I request GET "/audit/logon" endpoint without mandatory params
    Then HTTP "400" Bad Request response is returned
