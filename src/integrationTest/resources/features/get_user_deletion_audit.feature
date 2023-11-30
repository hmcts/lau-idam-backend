Feature: The applications's GET audit user deletions endpoint

  Background:
    Given LAU IdAm backend application is healthy
    And I save several IdAM user deletions to "/audit/deletedAccounts" endpoint using s2s with data:
      |user id|email address           |first name|last name|deletion timestamp       |
      |1      |john.smith@example.net  |John      |Smith    |2023-05-04T11:32:43.125Z |
      |9      |will.smith@example.net  |Will      |Smith    |2023-05-10T07:19:59.346Z |
      |3      |will.jones@example.net  |Will      |Jones    |2023-05-15T16:09:28.671Z |
      |4      |jack.allen@example.net  |Jack      |Allen    |2023-05-23T23:48:16.591Z |

  Scenario: The backend is able to find entry based on userId
    When I GET "/audit/deletedAccounts" passing params:
    | userId         | 1                   |
    | startTimestamp | 2023-04-04T12:23:34 |
    | endTimestamp   | 2023-06-04T12:23:34 |
    Then a single deleted account entry is returned with params "userId" "1"

  Scenario: The backend is able to find entry based on email
    When I GET "/audit/deletedAccounts" passing params:
    | emailAddress   | will.smith@example.net |
    | startTimestamp | 2023-04-04T12:23:34    |
    | endTimestamp   | 2023-06-04T12:23:34    |
    Then a single deleted account entry is returned with params "emailAddress" "will.smith@example.net"

  Scenario: The backend is able to find entries based on firstName
    When I GET "/audit/deletedAccounts" passing params:
    | firstName      | Will                   |
    | startTimestamp | 2023-04-04T12:23:34    |
    | endTimestamp   | 2023-06-04T12:23:34    |
    Then multiple deleted account entries are returned with params "firstName" "Will"

  Scenario: The backend is able to find entries based on lastName
    When I GET "/audit/deletedAccounts" passing params:
      | lastName       | Smith                  |
      | startTimestamp | 2023-04-04T12:23:34    |
      | endTimestamp   | 2023-06-04T12:23:34    |
    Then multiple deleted account entries are returned with params "lastName" "Smith"

  Scenario: The backend is able to find entry based on all params
    When I GET "/audit/deletedAccounts" passing params:
      | userId         | 1                      |
      | emailAddress   | john.smith@example.net |
      | firstName      | John                   |
      | lastName       | Smith                  |
      | startTimestamp | 2023-04-04T12:23:34    |
      | endTimestamp   | 2023-06-04T12:23:34    |
    Then a single deleted account entry is returned with params "userId" "1"
    And a single deleted account entry is returned with params "emailAddress" "john.smith@example.net"
    And a single deleted account entry is returned with params "firstName" "John"
    And a single deleted account entry is returned with params "lastName" "Smith"

  Scenario: The backend responds bad request on missing params
    When I GET "/audit/deletedAccounts" passing params:
      |startTime | 2023-04-04T12:23:34 |
      |endTime   | 2023-06-04T12:23:34 |
    Then 400 "Bad Request" response is returned

  Scenario: The backend is unable to process logon GET requests due to missing s2s
    When And I GET "/audit/deletedAccounts" without service authorization header
    Then HTTP "403" Forbidden response is returned


  Scenario: The backend is able to find all deleted user entry based on params
    When I GET "/audit/getAllDeletedAccounts" passing params:
      | page         | 1      |
      | size         | 100    |
      | sort         | asc    |
    Then multiple deleted account entries are returned with size 4


  Scenario: The backend is able to find all deleted user entry based on params without sort
    When I GET "/audit/getAllDeletedAccounts" passing params:
      | page         | 1      |
      | size         | 100    |
    Then multiple deleted account entries are returned with size 4


  Scenario: The backend is able to find all deleted user entry by asc
    When I GET "/audit/getAllDeletedAccounts" passing params:
      | page         | 1      |
      | size         | 100    |
      | sort         | asc    |
    Then multiple deleted account entries are returned with size 4
    And first deleted account entry is returned with params "userId" "1"
    And first deleted account entry is returned with params "emailAddress" "john.smith@example.net"
    And first deleted account entry is returned with params "firstName" "John"
    And first deleted account entry is returned with params "lastName" "Smith"


  Scenario: The backend is able to find all deleted user entry by desc
    When I GET "/audit/getAllDeletedAccounts" passing params:
      | page         | 1      |
      | size         | 100    |
      | sort         | desc   |
    Then multiple deleted account entries are returned with size 4
    And first deleted account entry is returned with params "userId" "4"
    And first deleted account entry is returned with params "emailAddress" "jack.allen@example.net"
    And first deleted account entry is returned with params "firstName" "Jack"
    And first deleted account entry is returned with params "lastName" "Allen"

  Scenario: The backend returns paged results sorted by default deletion timestamp desc
    When I GET "/audit/getAllDeletedAccounts" passing params:
      | page    | 1 |
      | size    | 2 |
    Then two results are returned on the page 1:
      |user id|email address           |first name|last name|deletion timestamp       |
      |4      |jack.allen@example.net  |Jack      |Allen    |2023-05-23T23:48:16.591Z |
      |3      |will.jones@example.net  |Will      |Jones    |2023-05-15T16:09:28.671Z |
    When I GET "/audit/getAllDeletedAccounts" passing params:
      | page    | 2 |
      | size    | 2 |
    Then two results are returned on the page 2:
      |user id|email address           |first name|last name|deletion timestamp       |
      |9      |will.smith@example.net  |Will      |Smith    |2023-05-10T07:19:59.346Z |
      |1      |john.smith@example.net  |John      |Smith    |2023-05-04T11:32:43.125Z |

  Scenario: The backend is unable to process get All deleted requests due to missing params
    When I GET "/audit/getAllDeletedAccounts" passing params:
      | sort         | asc    |
    Then 400 "Bad Request" response is returned

  Scenario: The backend is unable to process get All deleted request due to missing s2s
    When And I GET "/audit/getAllDeletedAccounts" without service authorization header
    Then HTTP "403" Forbidden response is returned
