package com.agi.blog.pages;

import com.agi.blog.utils.ElementMap;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SearchPage {
    private final Page page;
    private final ElementMap map = new ElementMap("search");

    public SearchPage(Page page) { this.page = page; }

    /**
     * Navigates directly to /?s={term} — reliable alternative to interacting with the
     * Astra overlay, which is CSS-controlled and may not be reachable in headless mode.
     */
    public void searchFor(String baseUrl, String term) {
        try {
            String encoded = URLEncoder.encode(term, StandardCharsets.UTF_8);
            page.navigate(baseUrl + "/?s=" + encoded);
            page.waitForLoadState();
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to search results for term: " + term, e);
        }
    }

    public void typeSearchTerm(String term) { findInput().fill(term); }

    public void submitByEnter() {
        findInput().press("Enter");
        page.waitForLoadState();
    }

    public void submitByButton() {
        for (String sel : map.getFallbacks("submit.button")) {
            try {
                Locator b = page.locator(sel).first();
                if (b.isVisible()) { b.click(); page.waitForLoadState(); return; }
            } catch (Exception ignored) {}
        }
        submitByEnter();
    }

    public boolean hasResults() {
        page.waitForLoadState();
        try {
            page.waitForSelector(map.get("result.items"),
                new Page.WaitForSelectorOptions()
                    .setTimeout(5000)
                    .setState(WaitForSelectorState.VISIBLE));
            return page.locator(map.get("result.items")).count() > 0;
        } catch (Exception e) { return false; }
    }

    public int getResultsCount() { return page.locator(map.get("result.items")).count(); }

    public List<String> getResultTitles() {
        return page.locator(map.get("result.titles")).allInnerTexts();
    }

    public String getFirstResultTitle() {
        List<String> t = getResultTitles();
        return t.isEmpty() ? "" : t.get(0).trim();
    }

    public boolean isNoResultsMessageVisible() {
        try {
            for (String sel : map.getFallbacks("no.results"))
                if (page.locator(sel).count() > 0) return true;
            String c = page.content().toLowerCase();
            return c.contains("nenhum resultado") || c.contains("nothing found") || c.contains("não encontramos");
        } catch (Exception e) { return false; }
    }

    public boolean allResultsHaveLinks() {
        List<Locator> links = page.locator(map.get("result.titles")).all();
        return !links.isEmpty() && links.stream()
                .allMatch(l -> l.getAttribute("href") != null && !l.getAttribute("href").isEmpty());
    }

    public String clickFirstResult() {
        Locator first = page.locator(map.get("result.titles")).first();
        String href = first.getAttribute("href");
        first.click();
        page.waitForLoadState();
        return href;
    }

    private Locator findInput() {
        for (String sel : map.getFallbacks("input")) {
            try {
                page.waitForSelector(sel,
                    new Page.WaitForSelectorOptions()
                        .setTimeout(3000)
                        .setState(WaitForSelectorState.VISIBLE));
                return page.locator(sel).first();
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("Search input not found. URL: " + page.url());
    }
}
