package example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Main2 {
    private static final Logger log = LogManager.getLogger(Main2.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        log.info("Running in Test layer - Test Main 2");

        SimpleHtmlReport report = new SimpleHtmlReport("reports","test selenium github actions");

        report.info("Test Main 2");

    }
}
