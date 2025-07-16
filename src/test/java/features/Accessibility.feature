Feature: Accessibility Testing on Practo

  Scenario: Screen reader compatibility
    Given I am on the homepage for accessibility testing
    Then the page should have ARIA attributes for screen reader compatibility

  Scenario: Keyboard-only accessibility
    Given I am on the homepage for accessibility testing
    Then the page should be navigable using keyboard only

  Scenario: Skip to content link presence
    Given I am on the homepage for accessibility testing
    Then the page should have a skip to content link
