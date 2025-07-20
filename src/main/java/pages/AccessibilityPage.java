package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class AccessibilityPage extends BasePage {

    public AccessibilityPage(WebDriver driver) {
        super(driver);
    }

    public boolean hasAriaAttributes() {
        List<WebElement> elements = driver.findElements(By.xpath("//*[@aria-label or @aria-labelledby or @role]"));
        return !elements.isEmpty();
    }

    public boolean hasTabbableElements() {
        List<WebElement> tabbable = driver.findElements(By.cssSelector("a, button, input, select, textarea, [tabindex]"));
        for (WebElement el : tabbable) {
            if (el.isDisplayed() && el.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSkipToContentLink() {
        List<WebElement> skipLinks = driver.findElements(By.cssSelector("a[href*='skip'], a[aria-label*='skip']"));
        return !skipLinks.isEmpty();
    }
}
