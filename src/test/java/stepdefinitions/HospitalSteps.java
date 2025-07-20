package stepdefinitions;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import pages.HomePage;
import utils.DriverFactory;

public class HospitalSteps {

    WebDriver driver = DriverFactory.getDriver();
    HomePage homePage = new HomePage(driver);
    pages.HospitalSearchPage hospitalSearchPage = new pages.HospitalSearchPage(driver);
    pages.DiagnosticsPage diagnosticsPage = new pages.DiagnosticsPage(driver);
    pages.CorporateWellnessPage corporateWellnessPage = new pages.CorporateWellnessPage(driver);

    @Given("I open Practo homepage")
    public void openPractoHomepage() {
        System.out.println("[Thread " + Thread.currentThread().getId() + "] - openPractoHomepage");
        homePage.goToHomePage();
    }

    @When("I search for {string}")
    public void searchForHospitals(String query) {
        System.out.println("[Thread " + Thread.currentThread().getId() + "] - searchForHospitals");
        hospitalSearchPage.searchAndSelectHospital();
    }

    @When("I apply filters for {int}\\/{int} service and parking")
    public void i_apply_filters_for_service_and_parking(Integer int1, Integer int2) {
        System.out.println("[Thread " + Thread.currentThread().getId() + "] - i_apply_filters_for_service_and_parking");
        hospitalSearchPage.apply24x7AndParkingFilters();
    }

    @Then("I should see a list of hospitals with rating greater than 3.5")
    public void extractHospitals() {
        System.out.println("[Thread " + Thread.currentThread().getId() + "] - extractHospitals");
        hospitalSearchPage.extractTopHospitals();
    }

    @Given("I navigate to the Diagnostics page")
    public void goToDiagnosticsPage() {
        System.out.println("[Thread " + Thread.currentThread().getId() + "] - goToDiagnosticsPage");
        diagnosticsPage.goToDiagnosticsPage();
    }

    @When("I extract the list of top cities")
    public void extractCities() {
        System.out.println("[Thread " + Thread.currentThread().getId() + "] - extractCities");
        diagnosticsPage.extractTopDiagnosticCities();
    }

    @Then("I display them in the console")
    public void displayCities() {
        System.out.println("[Thread " + Thread.currentThread().getId() + "] - displayCities");
    }

    @Given("I navigate to the Corporate Wellness page")
    public void goToCorporateWellness() {
        System.out.println("[Thread " + Thread.currentThread().getId() + "] - goToCorporateWellness");
        corporateWellnessPage.goToCorporateWellnessPage();
    }

    @When("I fill the form with invalid data")
    public void fillInvalidForm() {
        System.out.println("[Thread " + Thread.currentThread().getId() + "] - fillInvalidForm");
        corporateWellnessPage.fillInvalidCorporateForm();
    }

    @When("I click on Schedule a demo")
    public void clickScheduleDemo() {
        System.out.println("[Thread " + Thread.currentThread().getId() + "] - clickScheduleDemo");
        corporateWellnessPage.assertScheduleDemoButtonNotClickable();
    }

    @Then("I capture and display the warning message")
    public void captureWarning() {
        System.out.println("[Thread " + Thread.currentThread().getId() + "] - captureWarning");
        corporateWellnessPage.assertCorporateFormShowsError();
    }
}
