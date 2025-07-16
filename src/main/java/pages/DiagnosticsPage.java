package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

public class DiagnosticsPage extends BasePage {

    public DiagnosticsPage(WebDriver driver) {
        super(driver);
    }

    public void goToDiagnosticsPage() {
        driver.get("https://www.practo.com/tests");
        new WebDriverWait(driver, Duration.ofSeconds(15))
            .until(webDriver -> ((JavascriptExecutor) webDriver)
            .executeScript("return document.readyState").equals("complete"));
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {} // Wait for cities to appear
    }

    public void extractTopDiagnosticCities() {
        List<WebElement> cities = driver.findElements(By.cssSelector("ul.u-br-rule li .u-margint--standard.o-f-color--primary"));
        java.util.List<String> cityNames = new java.util.ArrayList<>();
        for (WebElement city : cities) {
            String name = city.getText().trim();
            if (!name.isEmpty()) cityNames.add(name);
        }
        java.io.File outFile = new java.io.File("diagnostic_cities.txt");
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(outFile))) {
            writer.println("Top Diagnostic Cities:");
            for (String city : cityNames) {
                writer.println(city);
            }
            System.out.println("Top Diagnostic Cities written to: " + outFile.getAbsolutePath());
        } catch (Exception fileEx) {
            System.out.println("❌ Could not write cities to file: " + fileEx.getMessage());
        }
        System.out.println("Top Diagnostic Cities:");
        for (String city : cityNames) {
            System.out.println(" - " + city);
        }
    }
}
