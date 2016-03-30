package com.yourcompany;

import com.saucelabs.common.SauceOnDemandAuthentication;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import java.net.URL;
import static org.junit.Assert.*;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;

public class SampleSauceTest implements SauceOnDemandSessionIdProvider {

    public String username = System.getenv("SAUCE_USERNAME");
    public String accesskey = System.getenv("SAUCE_ACCESS_KEY");
    public String os = System.getenv("SELENIUM_PLATFORM");
    public String browserName = System.getenv("SELENIUM_BROWSER");
    public String version = System.getenv("SELENIUM_VERSION");

    /**
     * Constructs a {@link SauceOnDemandAuthentication} instance using the supplied user name/access key.  To use the authentication
     * supplied by environment variables or from an external file, use the no-arg {@link SauceOnDemandAuthentication} constructor.
     */
    public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(username, accesskey);

    /**
     * JUnit Rule which will mark the Sauce Job as passed/failed when the test succeeds or fails.
     */
    @Rule
    public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

    @Rule public TestName name = new TestName() {
        public String getMethodName() {
        		return String.format("%s : (%s %s %s)", super.getMethodName(), os, browserName, version);
        };
    };

    private String sessionId;

    /**
     * The {@link WebDriver} instance which is used to perform browser interactions with.
     */
    private WebDriver driver;

    /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the {@link #browser},
     * {@link #version} and {@link #os} instance variables, and which is configured to run against ondemand.saucelabs.com, using
     * the username and access key populated by the {@link #authentication} instance.
     *
     * @throws Exception if an error occurs during the creation of the {@link RemoteWebDriver} instance.
     */
    @Before
    public void setUp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability(CapabilityType.BROWSER_NAME, browserName);
        capabilities.setCapability(CapabilityType.VERSION, version);
        capabilities.setCapability(CapabilityType.PLATFORM, os);

        String methodName = name.getMethodName();
        capabilities.setCapability("name", methodName);

        this.driver = new RemoteWebDriver(
                new URL("http://" + authentication.getUsername() + ":" + authentication.getAccessKey() +
                        "@ondemand.saucelabs.com:80/wd/hub"),
                capabilities);
        this.sessionId = (((RemoteWebDriver) driver).getSessionId()).toString();

        String message = String.format("SauceOnDemandSessionID=%1$s job-name=%2$s", this.sessionId, methodName);
        System.out.println(message);
    }

    /**
     * Runs a simple test verifying the UI and title of the belk.com home page.
     * @throws Exception
     */
    @Test
    public void verifyEmailInputTest() throws Exception {        
        String expectedTitle = "I am a page title - Sauce Labs";
        
        driver.get("https://saucelabs.com/test/guinea-pig");
        assertEquals(driver.getTitle(), expectedTitle);
 
    }

    /**
     * Closes the {@link WebDriver} session.
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    /**
     *
     * @return the value of the Sauce Job id.
     */
    @Override
    public String getSessionId() {
        return sessionId;
    }
}
