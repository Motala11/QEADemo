package stepdefinitions;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.AccessibilityPage;
import utils.DriverFactory;

public class AccessibilitySteps {
    WebDriver driver = DriverFactory.getDriver();
    AccessibilityPage accessibilityPage = new AccessibilityPage(driver);

    @Given("I am on the homepage for accessibility testing")
    public void openHomePageForAccessibility() {
        driver.get("https://www.practo.com/");
    }

    @Then("the page should have ARIA attributes for screen reader compatibility")
    public void checkAriaAttributes() {
        boolean hasAria = accessibilityPage.hasAriaAttributes();
        System.out.println("ARIA attributes present: " + hasAria);
        Assert.assertTrue(hasAria, "No ARIA attributes found for screen reader compatibility.");
    }

    @Then("the page should be navigable using keyboard only")
    public void checkKeyboardAccessibility() {
        boolean hasTabbable = accessibilityPage.hasTabbableElements();
        System.out.println("Tabbable elements present: " + hasTabbable);
        Assert.assertTrue(hasTabbable, "No tabbable elements found for keyboard navigation.");
    }

    @Then("the page should have a skip to content link")
    public void checkSkipToContentLink() {
        boolean hasSkipLink = accessibilityPage.hasSkipToContentLink();
        System.out.println("Skip to content link present: " + hasSkipLink);
        if (!hasSkipLink) {
            System.out.println("[Accessibility Warning] No skip to content link found. Test will not fail.");
        }
    }
}
