package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

public class CorporateWellnessPage extends BasePage {

    public CorporateWellnessPage(WebDriver driver) {
        super(driver);
    }

    public void goToCorporateWellnessPage() {
        driver.get("https://www.practo.com/plus/corporate");
        new WebDriverWait(driver, Duration.ofSeconds(15))
            .until(webDriver -> ((JavascriptExecutor) webDriver)
            .executeScript("return document.readyState").equals("complete"));
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
    }

    public void fillInvalidCorporateForm() {
        WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        localWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form//div[1]/input"))).sendKeys("123");
        localWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form//div[2]/input"))).sendKeys("!!");
        localWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("contactNumber"))).sendKeys("+91111");
        localWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("officialEmailId"))).sendKeys("example@example.com");
        try {
            WebElement orgSize = localWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("organizationSize")));
            if (orgSize.getTagName().equalsIgnoreCase("select")) {
                new org.openqa.selenium.support.ui.Select(orgSize).selectByIndex(1);
            } else {
                orgSize.click();
                Thread.sleep(300);
                List<WebElement> options = driver.findElements(By.xpath("//div[contains(@class,'select-option') or contains(@class,'option') or contains(@class,'menu')]//div | //li"));
                if (!options.isEmpty()) options.get(0).click();
            }
        } catch (Exception e) { System.out.println("[WARN] Could not select org size: " + e.getMessage()); }
        try {
            WebElement interested = localWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("interestedIn")));
            if (interested.getTagName().equalsIgnoreCase("select")) {
                new org.openqa.selenium.support.ui.Select(interested).selectByIndex(1);
            } else {
                interested.click();
                Thread.sleep(300);
                List<WebElement> options = driver.findElements(By.xpath("//div[contains(@class,'select-option') or contains(@class,'option') or contains(@class,'menu')]//div | //li"));
                if (!options.isEmpty()) options.get(0).click();
            }
        } catch (Exception e) { System.out.println("[WARN] Could not select 'interested in': " + e.getMessage()); }
    }

    public void assertScheduleDemoButtonNotClickable() {
        WebElement demoBtn = driver.findElement(By.xpath("//button[contains(text(),'Schedule a demo')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", demoBtn);
        boolean isDisabled = !demoBtn.isEnabled() || demoBtn.getAttribute("disabled") != null;
        if (!isDisabled) {
            throw new RuntimeException("Schedule a demo button should NOT be clickable for invalid input");
        } else {
            System.out.println("Schedule a demo button is correctly not clickable due to invalid data.");
        }
    }

    public void assertCorporateFormShowsError() {
        WebElement contactInput = driver.findElement(By.id("contactNumber"));
        String inputClass = contactInput.getAttribute("class");
        boolean hasErrorClass = inputClass != null && inputClass.contains("corporate-form__input--error");
        System.out.println("Contact number input error class present: " + hasErrorClass);
        if (!hasErrorClass) {
            throw new RuntimeException("Contact number input should have error class for invalid input");
        }

        WebElement demoBtn = driver.findElement(By.xpath("//button[contains(text(),'Schedule a demo')]"));
        String btnClass = demoBtn.getAttribute("class");
        boolean isDisabled = !demoBtn.isEnabled() || demoBtn.getAttribute("disabled") != null;
        boolean hasGreyClass = btnClass != null && btnClass.contains("bg-grey-3");
        System.out.println("Schedule a demo button disabled: " + isDisabled + ", has grey class: " + hasGreyClass);
        if (!(isDisabled && hasGreyClass)) {
            throw new RuntimeException("Schedule a demo button should be disabled and grey for invalid input");
        }
    }
}
