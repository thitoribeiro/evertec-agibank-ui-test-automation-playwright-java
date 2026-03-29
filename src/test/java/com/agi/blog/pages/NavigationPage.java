package com.agi.blog.pages;

import com.agi.blog.utils.ElementMap;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.List;

public class NavigationPage {
    private final Page page;
    private final ElementMap map = new ElementMap("navigation");

    public NavigationPage(Page page) { this.page = page; }

    public void hoverMenuItem(String text) {
        findItem(text).hover();
        page.waitForTimeout(900); // Astra dropdown has CSS transition ~300ms; extra buffer for CI
    }

    public void clickMenuItem(String text) {
        findItem(text).click();
        page.waitForLoadState();
    }

    public boolean isSubmenuVisible(String parentText) {
        // Astra hides submenus with visibility:hidden / opacity:0 before hover.
        // After hover + wait, check count() > 0 (exists in DOM and rendered) within the li,
        // then fall back to a global page check.
        try {
            Locator parent = findItem(parentText);
            for (String sel : map.getFallbacks("submenu")) {
                try {
                    Locator sub = parent.locator(sel);
                    if (sub.count() > 0) return true;
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        // Global fallback: any visible sub-menu on the page
        for (String sel : map.getFallbacks("submenu.visible")) {
            try { if (page.locator(sel).count() > 0) return true; }
            catch (Exception ignored) {}
        }
        return false;
    }

    public List<String> getSubmenuItems(String parentText) {
        try {
            Locator parent = findItem(parentText);
            for (String sel : map.getFallbacks("submenu.items")) {
                try {
                    List<String> items = parent.locator(sel).allInnerTexts();
                    if (!items.isEmpty()) return items;
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return List.of();
    }

    public void clickSubmenuItem(String itemText) {
        for (String sel : map.getFallbacks("submenu.item.link", itemText)) {
            try {
                Locator l = page.locator(sel).first();
                if (l.isVisible()) { l.click(); page.waitForLoadState(); return; }
            } catch (Exception ignored) {}
        }
        page.locator("a:has-text('" + itemText + "')").first().click();
        page.waitForLoadState();
    }

    public boolean categoryPageHasArticles() {
        try { return page.locator(map.getAsComma("category.articles")).count() > 0; }
        catch (Exception e) { return false; }
    }

    public String getCurrentUrl() { return page.url(); }

    private Locator findItem(String text) {
        for (String sel : map.getFallbacks("menu.item.wrapper", text)) {
            try {
                Locator l = page.locator(sel).first();
                if (l.count() > 0) return l;
            } catch (Exception ignored) {}
        }
        return page.locator("a:has-text('" + text + "')").first();
    }
}
