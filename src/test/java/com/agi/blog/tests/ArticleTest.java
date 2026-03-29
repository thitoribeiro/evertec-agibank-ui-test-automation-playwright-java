package com.agi.blog.tests;

import com.agi.blog.base.BaseTest;
import com.agi.blog.pages.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Blog do Agi")
@Feature("Leitura de Artigo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ArticleTest extends BaseTest {

    private ArticlePage articlePage;

    @BeforeEach
    void navigateToArticleViaSearch() {
        navigateToHome();
        new HomePage(page).clickSearchIcon();
        SearchPage sp = new SearchPage(page);
        sp.typeSearchTerm("empréstimo");
        sp.submitByEnter();
        assertTrue(sp.hasResults(), "Precondition failed: search must return results");
        sp.clickFirstResult();
        articlePage = new ArticlePage(page);
    }

    @Test @Order(1)
    @Story("Conteúdo do artigo") @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Clicar em resultado da busca abre o artigo correto")
    void shouldOpenArticleFromSearchResult() {
        assertTrue(articlePage.isTitleVisible(), "Article title should be visible");
        assertTrue(articlePage.hasArticleSlugInUrl(),
            "URL should represent an article. Got: " + articlePage.getCurrentUrl());
    }

    @Test @Order(2)
    @Story("Metadados do artigo") @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Artigo exibe título, autor e data de publicação")
    void shouldDisplayArticleMetadata() {
        assertAll("Article metadata",
            () -> assertTrue(articlePage.isTitleVisible(),     "Title should be visible"),
            () -> assertTrue(articlePage.hasAuthor(),          "Author should be displayed"),
            () -> assertTrue(articlePage.hasPublicationDate(), "Publication date should be displayed"));
    }

    @Test @Order(3)
    @Story("Navegação — breadcrumb") @Severity(SeverityLevel.NORMAL)
    @DisplayName("Breadcrumb exibe o caminho correto com link Home")
    void shouldDisplayCorrectBreadcrumb() {
        assertTrue(articlePage.hasBreadcrumb(), "Breadcrumb navigation should be present");
        assertTrue(articlePage.breadcrumbHasHomeLink(), "Breadcrumb should contain a 'Home' link");
    }

    @Test @Order(4)
    @Story("Validação de rota") @Severity(SeverityLevel.NORMAL)
    @DisplayName("URL da página corresponde ao slug do artigo")
    void shouldHaveArticleSlugInUrl() {
        String url = articlePage.getCurrentUrl();
        assertTrue(url.startsWith("https://blog.agibank.com.br/"), "URL should be on blog domain. Got: " + url);
        assertFalse(url.contains("?s="), "Article URL should not contain search params. Got: " + url);
    }

    @Test @Order(5)
    @Story("Conteúdo do artigo") @Severity(SeverityLevel.NORMAL)
    @DisplayName("Artigo contém corpo de texto com conteúdo legível")
    void shouldHaveReadableContent() {
        assertTrue(articlePage.hasContent(), "Article body should have readable text content (>50 chars)");
    }
}
