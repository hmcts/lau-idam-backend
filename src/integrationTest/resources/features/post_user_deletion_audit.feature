Feature: The application's POST user deletion audit endpoint

  Scenario: The backend is able to process IdAM user deletion POST requests
    Given LAU IdAm backend application is healthy
    When I POST IdAM user deletion data to "/audit/deletedAccounts" endpoint using s2s with data:
      |user id|email address       |first name|last name|deletion timestamp       |
      |1      |email1@example.net  |John      |Smith    |2023-05-20T11:32:43.433Z |
      |2      |email2@example.net  |Will      |Smith    |2023-05-19T07:32:43.433Z |
    Then "CREATED" response status returned
    And The same user deletions are returned:
      |user id|email address       |first name|last name|deletion timestamp       |
      |1      |email1@example.net  |John      |Smith    |2023-05-20T11:32:43.433Z |
      |2      |email2@example.net  |Will      |Smith    |2023-05-19T07:32:43.433Z |

  Scenario: The backend is unable to to process IdAM user deletion POST requests due to missing mandatory parameter
    Given LAU IdAm backend application is healthy
    When I POST IdAM user deletion data to "/audit/deletedAccounts" endpoint using s2s with data:
      |user id|email address      |first name|last name|deletion timestamp       |
      |1      |email@example.net  |John      |Smith    |2023-05-20T11:32:43.433Z |
      |2      |                   |Will      |Smith    |2023-05-19T07:32:43.433Z |
    Then "BAD_REQUEST" response status returned

  Scenario: The backend is to process IdAM user deletion POST requests due to invalid parameter
    Given LAU IdAm backend application is healthy
    When I POST IdAM user deletion data to "/audit/deletedAccounts" endpoint using s2s with data:
      |user id                                                                     |email address      |first name|last name|deletion timestamp       |
      |0123456789012345678901234567890123456789012345678901234567890123456789      |email@example.net  |John      |Smith    |2023-05-20T11:32:43.433Z |
    Then "BAD_REQUEST" response status returned
