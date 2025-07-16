package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Common navigation method
    public void goToUrl(String url) {
        driver.get(url);
    }

    // Common method to get page title
    public String getPageTitle() {
        return driver.getTitle();
    }

    // Add other common utilities as needed
}
