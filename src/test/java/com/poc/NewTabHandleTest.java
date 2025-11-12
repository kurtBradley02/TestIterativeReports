package com.poc;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.poc.tests.base.BaseTest;
import org.testng.annotations.Test;

public class NewTabHandleTest extends BaseTest {

    @Test
    public void openNewTab(){

        try {
            logger.info("🧭 Navigating to Windows demo page...");
            page.navigate("https://demo.automationtesting.in/Windows.html");
            logger.info("✅ Page loaded successfully.");

            logger.info("🪟 Clicking button to open a new window/tab...");
            Page page1 = page.waitForPopup(() -> {
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("click")).click();
            });
            logger.info("✅ Popup (new tab/window) detected and captured.");

            logger.info("🔗 Clicking 'Downloads' link inside the new page...");
            page1.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Downloads")).click();
            logger.info("✅ Successfully navigated to 'Downloads' page in the new tab/window.");

        } catch (Exception e) {
            logger.error("❌ Error while handling new window/tab: {}", e.getMessage(), e);
            throw e;
        }
    }


}
