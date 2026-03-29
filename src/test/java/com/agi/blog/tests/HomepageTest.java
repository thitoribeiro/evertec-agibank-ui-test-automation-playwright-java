package com.agi.blog.tests;

import com.agi.blog.base.BaseTest;
import com.agi.blog.pages.HomePage;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Blog do Agi")
@Feature("Homepage")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HomepageTest extends BaseTest {

    private HomePage homePage;

    @BeforeEach
    void setup() { navigateToHome(); homePage = new HomePage(page); }

    @Test @Order(1)
    @Story("Carregamento da página") @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Homepage carrega com título e logo visíveis")
    void shouldLoadHomepageWithLogoAndTitle() {
        assertAll("Homepage elements",
            () -> assertTrue(homePage.isPageTitleCorrect(), "Title should contain 'agi' or 'blog'"),
            () -> assertTrue(homePage.isLogoVisible(), "Logo should be visible in header"));
    }

    @Test @Order(2)
    @Story("Carrossel hero") @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Carrossel hero exibe ao menos um artigo com botão 'Ler mais'")
    void shouldDisplayHeroCarousel() {
        assertTrue(homePage.isHeroCarouselVisible(), "Hero carousel or featured article should be visible");
    }

    @Test @Order(3)
    @Story("Carrossel hero") @Severity(SeverityLevel.NORMAL)
    @DisplayName("Clicar em 'Ler mais' no carrossel abre o artigo")
    void shouldOpenArticleFromHeroReadMore() {
        String homeUrl = page.url();
        homePage.clickHeroReadMore();
        assertNotEquals(homeUrl, page.url(), "Should navigate away from homepage");
        assertTrue(page.url().contains("blog.agibank.com.br"), "Should stay on blog domain");
    }

    @Test @Order(4)
    @Story("Listagem de artigos") @Severity(SeverityLevel.NORMAL)
    @DisplayName("Seção 'Últimas do Blog' exibe cards com artigos")
    void shouldDisplayLatestArticlesSection() {
        int count = homePage.getArticleCardsCount();
        assertTrue(count > 0, "Should display at least one article card. Found: " + count);
    }
}
