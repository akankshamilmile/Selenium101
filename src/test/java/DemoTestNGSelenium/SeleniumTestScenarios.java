package DemoTestNGSelenium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.function.Supplier;

public class SeleniumTestScenarios {

    private WebDriver driver;
    private static final Logger logger = LogManager.getLogger(SeleniumTestScenarios.class);
    private final String username = "akanksha_milmile";
    private final String accessKey = "W5BMG2qJak6MnjJuBj554I9Dnzmyngv5uYlQRClykBSsJqkFcA";
    private final String gridURL = "@hub.lambdatest.com/wd/hub";
    private final String baseURL = "https://www.lambdatest.com/selenium-playground/";
    private final String simpleFormDemoURL = "simple-form-demo";
    private final String expectedTextboxValue = "Welcome to LambdaTest";
    private final String expectedSuccessMessage = "Thanks for contacting us, we will get back to you shortly.";
    private final String expectedValidationMessage = "Please fill in this field.";

    private SoftAssert softAssert;

    @Parameters({"browser", "version", "platform"})
    @BeforeMethod(description = "Setup WebDriver")
    public void setup(String browser, String version, String platform) {
        ThreadContext.put("browser", browser);
        ThreadContext.put("version", version);
        ThreadContext.put("platform", platform);

        MutableCapabilities capabilities = configureBrowserCapabilities(browser, version, platform);

        try {
            driver = new RemoteWebDriver(new URL("https://" + username + ":" + accessKey + gridURL), capabilities);
            logger.info("WebDriver initialized successfully.");
            driver.manage().window().maximize();
            driver.get(baseURL);
            logger.info("Navigated to URL: {}", baseURL);
        } catch (MalformedURLException e) {
            logger.error("Invalid Grid URL: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error during setup: {}", e.getMessage());
        }
    }

    private MutableCapabilities configureBrowserCapabilities(String browser, String version, String platform) {
        MutableCapabilities capabilities;
        switch (browser.toLowerCase()) {
            case "chrome":
                capabilities = new ChromeOptions();
                break;
            case "firefox":
                capabilities = new FirefoxOptions();
                break;
            case "edge":
                capabilities = new EdgeOptions();
                break;
            case "internetexplorer":
            case "ie":
                capabilities = new InternetExplorerOptions();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        capabilities.setCapability("browserVersion", version);
        capabilities.setCapability("platformName", platform);
        capabilities.setCapability("lt:options", new HashMap<String, Object>() {{
            put("username", username);
            put("accessKey", accessKey);
            put("build", "Selenium Build");
            put("name", "Selenium Test");
            put("network", true);
            put("console", true);
            put("terminal", true);
            put("video", true);
            put("resolution", "1920x1080");
        }});
        return capabilities;
    }

    @Test(timeOut=20000)
    public void testSimpleFormDemo() {
        ThreadContext.put("testName", "SimpleFormDemo");
        logger.info("Starting test: Simple Form Demo");

        navigateTo("Simple Form Demo");
        Assert.assertTrue(driver.getCurrentUrl().contains(simpleFormDemoURL), "URL validation failed");

        WebElement userMessageInput = findElement(By.id("user-message"));
        userMessageInput.sendKeys(expectedTextboxValue);
        logger.info("Entered text: {}", expectedTextboxValue);

        WebElement showInputButton = findElement(By.id("showInput"));
        showInputButton.click();
        logger.info("Clicked on 'Show Input' button");

        WebElement messageOutput = findElement(By.id("message"));
        String actualText = messageOutput.getText();
        Assert.assertEquals(actualText, expectedTextboxValue, "Textbox value mismatch");
        logger.info("Test passed: Textbox value matches expected");
        ThreadContext.clearMap();
    }

    @Test(timeOut=40000)
    public void testDragAndDropSlider() {
        ThreadContext.put("testName", "DragAndDropSlider");
        logger.info("Starting test: Drag & Drop Slider");

        navigateTo("Drag & Drop Sliders");

        WebElement slider = findElement(By.xpath("(//input[@type='range'])[3]"));
        adjustSlider(slider, By.id("rangeSuccess"), 95);
        logger.info("Slider successfully adjusted to 95");
        ThreadContext.clearMap();
    }

    @Test(timeOut=20000)
    public void testInputFormSubmit() {
        ThreadContext.put("testName", "SimpleFormDemo");
        logger.info("Starting test: Input Form Submit");

        softAssert = new SoftAssert();
        navigateTo("Input Form Submit");

        WebElement submitButton = findElement(By.xpath("(//button[@type='submit'])[2]"));
        submitButton.click();

        validateField(By.name("name"), expectedValidationMessage);

        fillInputForm();
        submitButton.click();

        WebElement successMessageElement = findElement(By.xpath("//p[@class='success-msg hidden']"));
        softAssert.assertEquals(successMessageElement.getText(), expectedSuccessMessage, "Success message mismatch");
        softAssert.assertAll();
        ThreadContext.clearMap();
    }

    private void navigateTo(String linkText) {
        WebElement link = findElement(By.linkText(linkText));
        link.click();
        logger.info("Navigated to {}", linkText);
    }

    private WebElement findElement(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            logger.info("Element found: {}", locator);
            return element;
        } catch (NoSuchElementException e) {
            logger.error("Element not found: {}", locator);
            throw e;
        }
    }
    private void validateField(By locator, String expectedMessage) {
        WebElement field = findElement(locator);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String validationMessage = (String) js.executeScript("return arguments[0].validationMessage;", field);
        softAssert.assertEquals(validationMessage, expectedMessage, "Validation message mismatch");
        logger.info("Validation message: {}", validationMessage);
    }

    private void fillInputForm() {
        WebElement name = findElement(By.xpath("//input[@name='name']"));
        WebElement email = findElement(By.xpath("//form[@id='seleniumform']//input[@name='email']"));
        WebElement password = findElement(By.xpath("//form[@id='seleniumform']//input[@name='password']"));
        WebElement company = findElement(By.xpath("//form[@id='seleniumform']//input[@name='company']"));
        WebElement website = findElement(By.xpath("//form[@id='seleniumform']//input[@name='website']"));
        WebElement country = findElement(By.xpath("//select[@name='country']"));
        WebElement city = findElement(By.xpath("//form[@id='seleniumform']//input[@name='city']"));
        WebElement address1 = findElement(By.xpath("//form[@id='seleniumform']//input[@name='address_line1']"));
        WebElement address2 = findElement(By.xpath("//form[@id='seleniumform']//input[@name='address_line2']"));
        WebElement state = findElement(By.xpath("//form[@id='seleniumform']//input[@id='inputState']"));
        WebElement zipcode = findElement(By.xpath("//form[@id='seleniumform']//input[@name='zip']"));

        name.sendKeys("abc");
        email.sendKeys("abc@example.com");
        password.sendKeys("password");
        company.sendKeys("ABC Company");
        website.sendKeys("https://pqr.com");
        city.sendKeys("Test City abc");
        address1.sendKeys("123456 Main Street");
        address2.sendKeys("Suite 100");
        state.sendKeys("XYZ State");
        zipcode.sendKeys("123456");

        Select countrySelect = new Select(country);
        countrySelect.selectByVisibleText("Albania");

        logger.info("Filled input form successfully");
    }

    private void adjustSlider(WebElement slider, By outputLocator, int targetValue) {
        long startTime = System.currentTimeMillis();
        int timeoutInSeconds = 20;
        int previousValue = -1;

        while (true) {
            int currentValue = Integer.parseInt(findElement(outputLocator).getText());

            if (currentValue == targetValue) {
                break;
            }

            if (currentValue == previousValue) {
                Assert.assertTrue(false,"Slider adjustment failed: Value is stuck at " + currentValue);
            }

            previousValue = currentValue;
            slider.sendKeys(Keys.ARROW_RIGHT);

            if (System.currentTimeMillis() - startTime > timeoutInSeconds * 1000) {
                throw new RuntimeException("Slider adjustment timed out: Target value not reached within " + timeoutInSeconds + " seconds");
            }
        }
    }

    @AfterMethod
    public void teardown() {
        logger.info("Starting teardown");
        if (driver != null) {
            driver.quit();
            logger.info("WebDriver closed successfully");
        } else {
            logger.warn("WebDriver was null. Nothing to close.");
        }
    }
}
