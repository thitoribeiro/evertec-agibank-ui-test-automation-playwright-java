package com.agi.blog.pages;

import com.agi.blog.utils.ElementMap;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.List;

public class PaginationPage {
    private final Page page;
    private final ElementMap map = new ElementMap("pagination");

    public PaginationPage(Page page) { this.page = page; }

    public boolean isPaginationVisible() {
        try { return page.locator(map.getAsComma("container")).count() > 0; }
        catch (Exception e) { return false; }
    }

    public boolean isNextButtonVisible() {
        try {
            for (String s : map.getFallbacks("next.button"))
                if (page.locator(s).count() > 0 && page.locator(s).first().isVisible()) return true;
            return false;
        } catch (Exception e) { return false; }
    }

    public boolean isPreviousButtonVisible() {
        try {
            for (String s : map.getFallbacks("prev.button"))
                if (page.locator(s).count() > 0 && page.locator(s).first().isVisible()) return true;
            return false;
        } catch (Exception e) { return false; }
    }

    public void clickNext() {
        for (String sel : map.getFallbacks("next.button")) {
            try {
                Locator l = page.locator(sel).first();
                if (l.isVisible()) { l.click(); page.waitForLoadState(); return; }
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("Next button not found");
    }

    public void clickPageNumber(int n) {
        for (String sel : map.getFallbacks("page.number.click", String.valueOf(n))) {
            try {
                Locator l = page.locator(sel).first();
                if (l.count() > 0) { l.click(); page.waitForLoadState(); return; }
            } catch (Exception ignored) {}
        }
        // Fallback: navigate directly to the page URL (WordPress standard structure)
        String base = page.url().replaceAll("/page/\\d+/?", "").replaceAll("/$", "");
        page.navigate(base + "/page/" + n + "/");
        page.waitForLoadState();
    }

    public String getCurrentPageNumber() {
        try { return page.locator(map.getAsComma("current.page")).first().innerText().trim(); }
        catch (Exception e) {
            String url = page.url();
            return url.contains("/page/") ? url.replaceAll(".*/page/(\\d+).*", "$1") : "1";
        }
    }

    public List<String> getArticleTitles() {
        return page.locator(map.get("article.titles")).allInnerTexts();
    }

    public String getCurrentUrl() { return page.url(); }

    public boolean urlContainsPage(int n) {
        String url = page.url();
        return url.contains("/page/" + n) || url.contains("paged=" + n);
    }
}
