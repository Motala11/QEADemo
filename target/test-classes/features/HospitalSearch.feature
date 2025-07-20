Feature: Hospital Search on Practo

  Scenario: Find hospitals in Bangalore that are open 24/7, have parking, and rating > 3.5
    Given I open Practo homepage
    When I search for "Hospitals in Bangalore"
    And I apply filters for 24/7 service and parking
    Then I should see a list of hospitals with rating greater than 3.5

  Scenario: Capture top diagnostics cities
    Given I navigate to the Diagnostics page
    When I extract the list of top cities
    Then I display them in the console

  
  Scenario: Invalid submission on Corporate Wellness page
    Given I navigate to the Corporate Wellness page
    When I fill the form with invalid data
    And I click on Schedule a demo
    Then I capture and display the warning message

