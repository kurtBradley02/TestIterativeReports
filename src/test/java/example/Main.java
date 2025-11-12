package example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        log.info("Running in Test Layer");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");     // 👈 headless mode
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1280,800");

        WebDriver driver = new ChromeDriver(options);

        SimpleHtmlReport report = new SimpleHtmlReport("reports","test selenium github actions");

        try {
            log.info("Navigating to Google...");
            report.info("Navigating to Google...");
            driver.get("https://www.google.com");


            Thread.sleep(1000);
            try {
                WebElement agree = driver.findElement(By.xpath("//button[normalize-space()='I agree' or normalize-space()='Accept all']"));
                agree.click();
                log.info("Accepted cookies popup.");
                report.info("Accepted cookies popup.");
            } catch (Exception e) {
                log.warn("No cookie popup found.");
                report.info("No cookie popup found.");
            }

            WebElement searchBox = driver.findElement(By.name("q"));
            log.info("Typing search query: test");
            report.info("Typing search query: test");
            searchBox.sendKeys("test");
            searchBox.sendKeys(Keys.ENTER);

            Thread.sleep(2000);
            log.info("Page title after search: {}", driver.getTitle());
            report.info("Page title after search: " + driver.getTitle());

        } catch (Exception e) {
            log.error("An error occurred during the test", e);
            report.fail("An error occurred during the test" + e);
        } finally {
            driver.quit();
            log.info("Browser closed.");
            report.info("Browser closed.");
        }
    }
}
