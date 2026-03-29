package com.agi.blog.pages;

import com.agi.blog.utils.ElementMap;
import com.microsoft.playwright.Page;
import java.util.List;

public class ArticlePage {
    private final Page page;
    private final ElementMap map = new ElementMap("article");

    public ArticlePage(Page page) { this.page = page; }

    public String getTitle() {
        for (String sel : map.getFallbacks("title")) {
            try {
                String t = page.locator(sel).first().innerText().trim();
                if (!t.isEmpty()) return t;
            } catch (Exception ignored) {}
        }
        return page.title().trim();
    }

    public boolean isTitleVisible() {
        try { return !getTitle().isEmpty(); } catch (Exception e) { return false; }
    }

    public boolean hasAuthor() {
        try { return page.locator(map.getAsComma("author")).count() > 0; }
        catch (Exception e) { return false; }
    }

    public boolean hasPublicationDate() {
        try { return page.locator(map.getAsComma("date")).count() > 0; }
        catch (Exception e) { return false; }
    }

    public boolean hasBreadcrumb() {
        try { return page.locator(map.getAsComma("breadcrumb")).count() > 0; }
        catch (Exception e) { return false; }
    }

    public boolean breadcrumbHasHomeLink() {
        try {
            List<String> items = page.locator(map.get("breadcrumb.items")).allInnerTexts();
            return items.stream().anyMatch(i ->
                i.equalsIgnoreCase("home") ||
                i.equalsIgnoreCase("início") ||
                i.equalsIgnoreCase("inicio"));
        } catch (Exception e) { return false; }
    }

    public String getCurrentUrl() { return page.url(); }

    public boolean hasArticleSlugInUrl() {
        String url = page.url();
        return !url.equals("https://blog.agibank.com.br/")
            && !url.equals("https://blog.agibank.com.br")
            && !url.contains("?s=")
            && url.contains("blog.agibank.com.br/");
    }

    public boolean hasContent() {
        try {
            String c = page.locator(map.get("content")).first().innerText().trim();
            return c.length() > 50;
        } catch (Exception e) { return false; }
    }
}
