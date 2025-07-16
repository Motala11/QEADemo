package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.interactions.Actions;


public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void goToHomePage() {
        driver.get("https://www.practo.com/");
        // Wait for page to fully load
        new WebDriverWait(driver, Duration.ofSeconds(15))
            .until(webDriver -> ((JavascriptExecutor) webDriver)
            .executeScript("return document.readyState").equals("complete"));
    }

    // HomePage now only contains navigation and site-wide actions.
    // See HospitalSearchPage, DiagnosticsPage, and CorporateWellnessPage for scenario-specific logic.
}
