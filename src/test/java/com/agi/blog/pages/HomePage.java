package com.agi.blog.pages;

import com.agi.blog.utils.ElementMap;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.List;

public class HomePage {
    private final Page page;
    private final ElementMap map = new ElementMap("homepage");

    public HomePage(Page page) { this.page = page; }

    public boolean isLogoVisible() {
        try { return page.locator(map.getAsComma("logo")).first().isVisible(); }
        catch (Exception e) { return false; }
    }

    public boolean isPageTitleCorrect() {
        String title = page.title().toLowerCase();
        return title.contains("agi") || title.contains("blog");
    }

    public void clickSearchIcon() {
        for (String sel : map.getFallbacks("search.icon")) {
            try {
                Locator l = page.locator(sel).first();
                if (l.isVisible()) { l.click(); return; }
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("Search icon not found on: " + page.url());
    }

    public boolean isHeroCarouselVisible() {
        try {
            for (String sel : map.getFallbacks("hero.carousel"))
                if (page.locator(sel).count() > 0) return true;
            return page.locator("a:has-text('Ler mais'), a:has-text('Ler texto')").count() > 0;
        } catch (Exception e) { return false; }
    }

    public void clickHeroReadMore() {
        for (String sel : map.getFallbacks("hero.read.more")) {
            try {
                Locator l = page.locator(sel).first();
                if (l.isVisible()) { l.click(); return; }
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("Hero 'Ler mais' button not found");
    }

    public int getArticleCardsCount() {
        try { return page.locator(map.getAsComma("article.cards")).count(); }
        catch (Exception e) { return 0; }
    }

    public List<String> getLatestArticleTitles() {
        return page.locator(map.get("article.titles")).allInnerTexts();
    }
}
