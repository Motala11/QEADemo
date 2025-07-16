package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;

public class HospitalSearchPage extends BasePage {

    public HospitalSearchPage(WebDriver driver) {
        super(driver);
    }

    public void searchAndSelectHospital() {
        // Always set city to Bangalore from dropdown, using JS click for reliability
        WebElement cityBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[data-qa-id='omni-searchbox-locality']")));
        int maxAttempts = 3;
        boolean citySelected = false;
        for (int attempt = 1; attempt <= maxAttempts && !citySelected; attempt++) {
            cityBox.click();
            cityBox.clear();
            cityBox.sendKeys("Bangalore");
            try {
                // Try "Search in entire Bangalore" first
                WebElement suggestion = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@data-qa-id='omni-suggestion-entire-city' and contains(text(),'Search in entire Bangalore')]")
                ));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", suggestion);
                wait.until(ExpectedConditions.attributeToBe(cityBox, "value", "Bangalore"));
                citySelected = true;
                Thread.sleep(500);
            } catch (Exception e1) {
                try {
                    // Fallback: try any suggestion containing 'Bangalore'
                    WebElement suggestion2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@data-qa-id,'omni-suggestion') and contains(text(),'Bangalore')]")
                    ));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", suggestion2);
                    wait.until(ExpectedConditions.attributeToBe(cityBox, "value", "Bangalore"));
                    citySelected = true;
                    Thread.sleep(500);
                } catch (Exception e2) {
                    if (attempt == maxAttempts) {
                        System.out.println("[ERROR] Could not select Bangalore after " + maxAttempts + " attempts");
                        cityBox.sendKeys(Keys.ENTER);
                    } else {
                        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                    }
                }
            }
        }

        // Always set search to "Hospital" from dropdown (regardless of query)
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[data-qa-id='omni-searchbox-keyword']")));
        // Only type 'Hospital' once, then process suggestions
        searchBox.clear();
        // Type 'Hosp' one character at a time with a delay to trigger correct suggestions
        String hosp = "Hosp";
        for (char c : hosp.toCharArray()) {
            searchBox.sendKeys(Character.toString(c));
            try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        }
        // Optionally, wait a bit for suggestions to update
        try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        try {
            List<WebElement> suggestions = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div[data-qa-id='omni-suggestion-listing']")));
            System.out.println("[DEBUG] Suggestions for 'Hospital':");
            int idxToClick = -1;
            for (int i = 0; i < suggestions.size(); i++) {
                try {
                    WebElement main = suggestions.get(i).findElement(By.cssSelector("div[data-qa-id='omni-suggestion-main']"));
                    String mainText = main.getText().trim();
                    String rightText = "";
                    try {
                        WebElement right = suggestions.get(i).findElement(By.cssSelector("span[data-qa-id='omni-suggestion-right']"));
                        rightText = right.getText().trim();
                    } catch (Exception ignore2) {}
                    System.out.println("  - [" + (i+1) + "] main: '" + mainText + "', right: '" + rightText + "'");
                    if (mainText.equalsIgnoreCase("Hospital") && rightText.equalsIgnoreCase("TYPE")) {
                        idxToClick = i;
                    }
                } catch (Exception ignore) {}
            }
            if (idxToClick == -1 && suggestions.size() >= 4) {
                idxToClick = 3;
                System.out.println("[DEBUG] No semantic match, will click 4th suggestion.");
            } else if (idxToClick == -1 && suggestions.size() > 0) {
                idxToClick = suggestions.size() - 1;
                System.out.println("[DEBUG] No semantic match, will click last suggestion.");
            }
            if (idxToClick != -1) {
                System.out.println("[DEBUG] Clicking suggestion #" + (idxToClick+1) + ": " + suggestions.get(idxToClick).getText());
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", suggestions.get(idxToClick));
                Thread.sleep(200);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", suggestions.get(idxToClick));
            } else {
                System.out.println("[DEBUG] No suggestions found to click.");
            }
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("[DEBUG] Could not find or click suggestion: " + e.getMessage());
            searchBox.sendKeys(Keys.ENTER);
        }
        // Re-locate the search box after clicking to avoid stale element
        try {
            searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[data-qa-id='omni-searchbox-keyword']")));
            System.out.println("[DEBUG] Final search box value: " + searchBox.getAttribute("value"));
        } catch (Exception e) {
            System.out.println("[DEBUG] Could not re-locate search box after click: " + e.getMessage());
        }
        // Wait for the results page to load
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
            .executeScript("return document.readyState").equals("complete"));
    }

    public void apply24x7AndParkingFilters() {
        // Robust selectors for 24x7 and Parking filters
        boolean found24x7 = false, foundParking = false;
        try {
            // Practo uses input[type=checkbox] + label for filters, so find by label[for] or aria-label
            List<WebElement> allLabels = driver.findElements(By.cssSelector("label, [aria-label]"));
            for (WebElement label : allLabels) {
                String text = label.getText().trim().toLowerCase();
                if (!found24x7 && (text.contains("24x7") || text.contains("24/7") || text.contains("open 24"))) {
                    // Try clicking the label or its associated input
                    try {
                        label.click();
                        found24x7 = true;
                        Thread.sleep(500);
                    } catch (Exception e) {
                        // Try clicking the input if label fails
                        try {
                            String forAttr = label.getAttribute("for");
                            if (forAttr != null) {
                                WebElement cb = driver.findElement(By.id(forAttr));
                                if (!cb.isSelected()) cb.click();
                                found24x7 = true;
                                Thread.sleep(500);
                            }
                        } catch (Exception ignored2) {}
                    }
                }
                if (!foundParking && (text.contains("parking") || text.contains("has parking") || text.contains("with parking"))) {
                    try {
                        label.click();
                        foundParking = true;
                        Thread.sleep(500);
                    } catch (Exception e) {
                        try {
                            String forAttr = label.getAttribute("for");
                            if (forAttr != null) {
                                WebElement cb = driver.findElement(By.id(forAttr));
                                if (!cb.isSelected()) cb.click();
                                foundParking = true;
                                Thread.sleep(500);
                            }
                        } catch (Exception ignored2) {}
                    }
                }
                if (found24x7 && foundParking) break;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Exception while applying filters: " + e.getMessage());
        }
        if (!found24x7) System.out.println("⚠️ Could not find 24x7 filter label");
        if (!foundParking) System.out.println("⚠️ Could not find Parking filter label");
    }

    public void extractTopHospitals() {
        // Extract first 5 hospitals with rating >= 3.5, "Open 24x7", and Parking in amenities
        int found = 0;
        int page = 1;
        java.io.File outFile = new java.io.File("hospitals_output.txt");
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(outFile))) {
            writer.println("Hospital Name,Rating,Open 24x7,Parking");
            while (found < 5) {
                List<WebElement> hospitals = driver.findElements(By.cssSelector("div.c-estb-card"));
                boolean anyProcessed = false;
                for (int i = 0; i < hospitals.size() && found < 5; i++) {
                    try {
                        hospitals = driver.findElements(By.cssSelector("div.c-estb-card"));
                        WebElement hospital = hospitals.get(i);
                        String name = "";
                        double rating = 0.0;
                        boolean open247 = false;
                        // Name
                        try {
                            name = hospital.findElement(By.cssSelector(".c-estb-info h2.line-1")).getText();
                        } catch (Exception ignore) {}
                        // Rating
                        try {
                            String ratingStr = hospital.findElement(By.cssSelector(".col-3 .c-feedback .text-1 > span.u-bold")).getText().trim();
                            rating = Double.parseDouble(ratingStr);
                        } catch (Exception ignore) {}
                        // Open 24x7
                        try {
                            String openText = hospital.findElement(By.cssSelector(".line-4 .pd-right-2px-text-green")).getText();
                            if (openText != null && openText.toLowerCase().contains("24x7")) {
                                open247 = true;
                            }
                        } catch (Exception ignore) {}
                        if (open247 && rating >= 3.5) {
                            anyProcessed = true;
                            // Click to open hospital details in a new tab/window
                            try {
                                ((JavascriptExecutor) driver).executeScript("window.open(arguments[0].querySelector('a').href, '_blank');", hospital);
                            } catch (Exception e) {
                                // fallback: try clicking the card
                                try { hospital.click(); } catch (Exception ignore2) {}
                            }
                            // Switch to new tab
                            String originalWindow = driver.getWindowHandle();
                            for (String handle : driver.getWindowHandles()) {
                                if (!handle.equals(originalWindow)) {
                                    driver.switchTo().window(handle);
                                    break;
                                }
                            }
                            // Handle cookies/consent popup if present (try multiple selectors)
                            try {
                                WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                                // Try the provided absolute XPath for consent
                                try {
                                    WebElement consentBtn = popupWait.until(ExpectedConditions.elementToBeClickable(
                                        By.xpath("/html/body/div[6]/div[2]/div[2]/div[3]/div[2]/button[1]")));
                                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", consentBtn);
                                    Thread.sleep(300);
                                    System.out.println("[INFO] Closed consent/cookie popup with absolute XPath.");
                                } catch (Exception eAbs) {
                                    // Fallback to previous logic if absolute XPath fails
                                    By[] consentSelectors = new By[] {
                                        By.xpath("//button[contains(text(),'Accept')]"),
                                        By.xpath("//button[contains(text(),'Got it')]"),
                                        By.xpath("//button[contains(text(),'OK')]"),
                                        By.xpath("//button[contains(text(),'Allow all') or contains(text(),'Allow All')]"),
                                        By.cssSelector("button[aria-label*='Accept']"),
                                        By.cssSelector("button[aria-label*='Consent']"),
                                        By.cssSelector("button[title*='Accept']"),
                                        By.cssSelector("button[title*='Consent']"),
                                        By.cssSelector("button.cookie-accept, button#cookie-accept, .cookie-accept, .accept-cookies, .consent-accept")
                                    };
                                    boolean clicked = false;
                                    for (By selector : consentSelectors) {
                                        try {
                                            WebElement btn = popupWait.until(ExpectedConditions.elementToBeClickable(selector));
                                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                                            Thread.sleep(300);
                                            System.out.println("[INFO] Closed consent/cookie popup with selector: " + selector);
                                            clicked = true;
                                            break;
                                        } catch (Exception ignore) {}
                                    }
                                    if (!clicked) {
                                        List<WebElement> allBtns = driver.findElements(By.xpath("//button | //a"));
                                        for (WebElement btn : allBtns) {
                                            String cls = btn.getAttribute("class");
                                            String id = btn.getAttribute("id");
                                            String txt = btn.getText();
                                            if (((cls != null && (cls.toLowerCase().contains("cookie") || cls.toLowerCase().contains("consent"))) ||
                                                 (id != null && (id.toLowerCase().contains("cookie") || id.toLowerCase().contains("consent"))))
                                                 && btn.isDisplayed() && btn.isEnabled()) {
                                                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                                                Thread.sleep(300);
                                                System.out.println("[INFO] Closed consent/cookie popup with fallback button: " + txt);
                                                break;
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                // Popup not present, continue
                            }
                            // Wait for "Read more info" and click using robust selector
                            try {
                                WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(10));
                                WebElement readMore = localWait.until(ExpectedConditions.elementToBeClickable(
                                    By.cssSelector("span[data-qa-id='read_more_info']")
                                ));
                                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", readMore);
                                readMore.click();
                                Thread.sleep(500);
                            } catch (Exception e) {
                                System.out.println("[WARN] Could not click 'Read more info': " + e.getMessage());
                            }
                            // Check amenities for Parking
                            boolean hasParking = false;
                            try {
                                List<WebElement> amenities = driver.findElements(By.cssSelector("div[data-qa-id='amenities_list'] span[data-qa-id='amenity_item']"));
                                for (WebElement amenity : amenities) {
                                    if (amenity.getText().trim().equalsIgnoreCase("Parking")) {
                                        hasParking = true;
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("[WARN] Could not extract amenities: " + e.getMessage());
                            }
                            // Close the tab and switch back
                            driver.close();
                            driver.switchTo().window(originalWindow);
                            if (hasParking) {
                                System.out.println("✅ " + name + " - Rating: " + rating + " [Open 24x7, Parking]");
                                writer.println("\"" + name.replace("\"", "'") + "\"," + rating + "," + open247 + "," + hasParking);
                                found++;
                            }
                        }
                    } catch (StaleElementReferenceException se) {
                        i--;
                    } catch (Exception e) {
                        // skip if any info not found
                    }
                }
                // If not enough found and there is a next page, go to next page
                if (found < 5) {
                    // Try to click next page button if present
                    try {
                        WebElement nextBtn = driver.findElement(By.cssSelector("a[aria-label='Next']"));
                        if (nextBtn.isDisplayed() && nextBtn.isEnabled()) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", nextBtn);
                            nextBtn.click();
                            Thread.sleep(2000); // Wait for next page to load
                            page++;
                            continue;
                        }
                    } catch (Exception e) {
                        // No next button, break
                        break;
                    }
                } else {
                    break;
                }
            }
            if (found == 0) {
                System.out.println("⚠️ No hospitals found with Open 24x7, rating >= 3.5, and Parking");
            } else {
                System.out.println("\nResults also written to: " + outFile.getAbsolutePath());
            }
        } catch (Exception fileEx) {
            System.out.println("❌ Could not write to file: " + fileEx.getMessage());
        }
    }
}
