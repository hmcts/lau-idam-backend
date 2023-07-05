Feature: The application's DELETE user deletion endpoint

  Background:
    Given LAU IdAm backend application is healthy

  Scenario: The backend is able to process user deletion DELETE requests
    When I POST IdAM user deletion data to "/audit/deletedAccounts" endpoint using s2s with data:
      |user id |email address       |first name|last name|deletion timestamp       |
      |12345   |email1@example.net  |John      |Smith    |2023-05-20T11:32:43.433Z |
    And I request DELETE "/audit/idamUser/deleteIdamUserRecord" with user id "12345"
    Then 204 response is returned
    And An empty GET "/audit/deletedAccounts" response returned for the params:
      | userId         | 12345               |
      | startTimestamp | 2023-04-04T12:23:34 |
      | endTimestamp   | 2023-06-04T12:23:34 |

  Scenario: The backend is unable to process DELETE requests due to missing s2s header
    When I request DELETE "/audit/idamUser/deleteIdamUserRecord" endpoint with missing s2s header
    Then HTTP "403" Forbidden response is returned

  Scenario: The backend is unable to process logon DELETE requests due to invalid authorization header
    When I request DELETE "/audit/idamUser/deleteIdamUserRecord" endpoint with missing authorization header
    Then HTTP "401" Unauthorized response is returned

  Scenario: The backend is unable to process logon DELETE requests due invalid user id
    When I request DELETE "/audit/idamUser/deleteIdamUserRecord" with user id "00000"
    Then HTTP "404" "Not Found" response is returned

  Scenario: The backend is unable to process logon DELETE requests due invalid user id
    When I request DELETE "/audit/idamUser/deleteIdamUserRecord" with user id ""
    Then HTTP "400" "Bad Request" response is returned

