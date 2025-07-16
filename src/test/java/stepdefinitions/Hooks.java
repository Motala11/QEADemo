package stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import utils.DriverFactory;

public class Hooks {

    @Before
    public void setUp() {
        DriverFactory.getDriver(); // Initializes Chrome
    }

    @After
    public void tearDown() {
        DriverFactory.quitDriver(); // Quits Chrome
    }
}
