package com.poc.base;

import com.microsoft.playwright.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BaseTest {
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected String dataFilePath;

    private static final Path REPORTS_DIR = Paths.get("reports");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMddyyyy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HHmmss");

    @BeforeTest
    public void setup() {
        logger.info("Reading Config...");

        String filePath = "src/main/resources/config.properties";
        String fileContent = "";

        try {
            fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
            logger.info("✅ Config file loaded: {}", filePath);
        } catch (IOException e) {
            logger.error("❌ Error reading config file: {}", e.getMessage());
        }

        String[] entries = fileContent.split("[,\n\r]+");

        boolean headless = false;

        for (String entry : entries) {
            String[] kv = entry.trim().split("=");
            if (kv.length != 2) continue;

            String key = kv[0].trim();
            String value = kv[1].trim();

            if (key.equalsIgnoreCase("headless")) {
                headless = Boolean.parseBoolean(value);
            } else if (key.equalsIgnoreCase("testdata")) {
                this.dataFilePath = value;
            }
        }

        logger.info("Initializing Playwright...");

        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(headless));

        context = browser.newContext();

        // Ensure reports folder exists
        try {
            Files.createDirectories(REPORTS_DIR);
        } catch (IOException e) {
            logger.warn("Could not create reports directory: {}", e.getMessage());
        }

        // Start Playwright tracing
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        page = context.newPage();

        logger.info("✅ Browser launched successfully");
    }

    @AfterTest
    public void teardown() {
        try {
            Path tracePath = buildTracePath();
            logger.info("🧩 Stopping Playwright trace and exporting to {} ...", tracePath);
            context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
        } catch (Exception e) {
            logger.error("Trace export failed: {}", e.getMessage(), e);
        }

        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();

        logger.info("🧹 Playwright resources cleaned up successfully.");
    }

    /** Build a unique, iterative trace filename using PHT date + time + millis and some helpful context. */
    private Path buildTracePath() {
        // Use Manila time for the timestamp
        ZoneId zone = ZoneId.of("Asia/Manila");
        ZonedDateTime now = ZonedDateTime.now(zone);

        String date = now.format(DATE);          // MMddyyyy
        String time = now.format(TIME);          // HHmmss
        String millis = String.valueOf(System.currentTimeMillis() % 1000);

        // Optional: include class name and CI run id/attempt if present
        String className = getClass().getSimpleName();
        String runId = System.getenv().getOrDefault("GITHUB_RUN_ID", "local");
        String attempt = System.getenv().getOrDefault("GITHUB_RUN_ATTEMPT", "1");

        // trace-<class>-<MMddyyyy>_<HHmmss>_<millis>-run<id>a<attempt>.zip
        String filename = String.format(
                "trace-%s-%s_%s_%s-run%sa%s.zip",
                className, date, time, millis, runId, attempt
        );

        return REPORTS_DIR.resolve(filename);
    }

    public String[] getData(String sheetName, int rowNum) {
        String filePath = dataFilePath;
        String[] data = null;

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) return new String[0];

            Row row = sheet.getRow(rowNum - 1);
            if (row == null) return new String[0];

            int cellCount = row.getLastCellNum();
            data = new String[cellCount];

            DataFormatter formatter = new DataFormatter();
            for (int i = 0; i < cellCount; i++) {
                Cell cell = row.getCell(i);
                data[i] = (cell == null) ? "" : formatter.formatCellValue(cell);
            }

        } catch (Exception e) {
            logger.error("❌ Error reading Excel data: " + e.getMessage());
        }

        return data;
    }
}
