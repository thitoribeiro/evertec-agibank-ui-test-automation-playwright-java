package com.agi.blog.base;

import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import java.io.ByteArrayInputStream;

public abstract class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    protected static final String BASE_URL  = System.getProperty("base.url",  "https://blog.agibank.com.br");
    protected static final boolean HEADLESS = Boolean.parseBoolean(System.getProperty("headless", "true"));
    protected static final int SLOW_MO      = Integer.parseInt(System.getProperty("slow.mo", "0"));
    protected static final int TIMEOUT      = Integer.parseInt(System.getProperty("timeout", "30000"));

    @BeforeEach
    void setUp(TestInfo testInfo) {
        playwright = Playwright.create();
        BrowserType browserType = resolveBrowserType();
        browser = browserType.launch(new BrowserType.LaunchOptions()
                .setHeadless(HEADLESS).setSlowMo(SLOW_MO));
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 720)
                .setLocale("pt-BR")
                .setIgnoreHTTPSErrors(true));
        context.setDefaultTimeout(TIMEOUT);
        page = context.newPage();
        System.out.println("[TEST START] " + testInfo.getDisplayName());
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        try {
            if (page != null) {
                byte[] ss = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                String name = "screenshot-" + testInfo.getDisplayName()
                        .replaceAll("[^a-zA-Z0-9]", "_") + ".png";
                Allure.addAttachment(name, new ByteArrayInputStream(ss));
            }
        } catch (Exception ignored) {}
        if (page      != null) page.close();
        if (context   != null) context.close();
        if (browser   != null) browser.close();
        if (playwright != null) playwright.close();
        System.out.println("[TEST END] " + testInfo.getDisplayName());
    }

    private BrowserType resolveBrowserType() {
        return switch (System.getProperty("browser", "chromium").toLowerCase()) {
            case "firefox" -> playwright.firefox();
            case "webkit"  -> playwright.webkit();
            default        -> playwright.chromium();
        };
    }

    protected void navigateToHome() {
        page.navigate(BASE_URL);
        page.waitForLoadState();
    }
}
