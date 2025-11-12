package com.poc;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.poc.base.BaseTest;
import org.testng.annotations.Test;

public class PopUpHandleTest extends BaseTest {

    @Test
    public void openNewWindow(){

        try {
            logger.info("🧭 Navigating to Windows demo page...");
            page.navigate("https://demo.automationtesting.in/Windows.html");
            logger.info("✅ Page loaded successfully.");

            logger.info("📂 Opening 'Open New Seperate Windows' section...");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Open New Seperate Windows")).click();

            logger.info("🪟 Clicking button to open new window...");
            Page page1 = page.waitForPopup(() -> {
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("click")).click();
            });
            logger.info("✅ New window detected and captured.");

            logger.info("🔗 Clicking 'Downloads' link in the new window...");
            page1.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Downloads")).click();
            logger.info("✅ Successfully navigated to 'Downloads' page inside new window.");

        } catch (Exception e) {
            logger.error("❌ Error while handling new window: {}", e.getMessage(), e);
            throw e;
        }

    }

}
