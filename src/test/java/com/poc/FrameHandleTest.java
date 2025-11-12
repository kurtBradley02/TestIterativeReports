package com.poc;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import com.poc.base.BaseTest;
import org.testng.annotations.Test;

public class FrameHandleTest extends BaseTest {

    @Test
    public void testCase01(){

        logger.info("🧩 Navigating to Frames page...");
        page.navigate("https://demo.automationtesting.in/Frames.html");
        logger.info("✅ Page loaded: Frames demo site");

// Switch to frame and interact
        logger.info("🔍 Locating iframe: SingleFrame...");
        Locator frame1TextBox = page.locator("iframe[name='SingleFrame']")
                .contentFrame()
                .getByRole(AriaRole.TEXTBOX);

        logger.info("🖱️ Clicking inside the frame’s textbox...");
        frame1TextBox.click();

        String inputValue = "Test";
        logger.info("⌨️ Filling text '{}' inside the frame textbox...", inputValue);
        frame1TextBox.fill(inputValue);

        logger.info("✅ Successfully filled text '{}' into SingleFrame textbox.", inputValue);


    }

}
