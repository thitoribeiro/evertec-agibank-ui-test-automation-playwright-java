package com.agi.blog.tests;

import com.agi.blog.base.BaseTest;
import com.agi.blog.pages.PaginationPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Blog do Agi")
@Feature("Paginação de Artigos")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaginationTest extends BaseTest {

    private PaginationPage pagination;

    @BeforeEach
    void setup() { navigateToHome(); pagination = new PaginationPage(page); }

    @Test @Order(1)
    @Story("Próxima página") @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Clicar em 'Próxima' avança para página 2 com artigos diferentes")
    void shouldLoadPage2WithDifferentArticles() {
        assertTrue(pagination.isPaginationVisible(), "Pagination should be visible on homepage");
        List<String> titlesPage1 = pagination.getArticleTitles();
        assertTrue(pagination.isNextButtonVisible(), "'Próxima »' button should be visible on page 1");
        pagination.clickNext();
        List<String> titlesPage2 = pagination.getArticleTitles();
        assertFalse(titlesPage2.isEmpty(), "Page 2 should display articles");
        assertNotEquals(titlesPage1, titlesPage2, "Articles on page 2 should differ from page 1");
    }

    @Test @Order(2)
    @Story("Acesso direto por número") @Severity(SeverityLevel.NORMAL)
    @DisplayName("Clicar diretamente no número 3 exibe artigos da página 3")
    void shouldNavigateToPage3Directly() {
        List<String> titlesPage1 = pagination.getArticleTitles();
        pagination.clickPageNumber(3);
        List<String> titlesPage3 = pagination.getArticleTitles();
        assertFalse(titlesPage3.isEmpty(), "Page 3 should display articles");
        assertNotEquals(titlesPage1, titlesPage3, "Articles on page 3 should differ from page 1");
    }

    @Test @Order(3)
    @Story("Edge case — primeira página") @Severity(SeverityLevel.NORMAL)
    @DisplayName("Na página 1 o botão 'Anterior' não está presente")
    void shouldNotShowPreviousButtonOnPage1() {
        assertFalse(pagination.isPreviousButtonVisible(),
            "'« Anterior' button should NOT be visible on page 1");
    }

    @Test @Order(4)
    @Story("Validação de rota") @Severity(SeverityLevel.MINOR)
    @DisplayName("URL atualiza com parâmetro de página ao navegar para página 2")
    void shouldUpdateUrlWithPageParameter() {
        pagination.clickNext();
        String url = pagination.getCurrentUrl();
        assertTrue(url.contains("/page/2") || url.contains("paged=2") || url.contains("page=2"),
            "URL should contain page 2 indicator. Got: " + url);
    }

    @Test @Order(5)
    @Story("Edge case — página 2") @Severity(SeverityLevel.MINOR)
    @DisplayName("Na página 2 o botão 'Anterior' está visível e retorna à página 1")
    void shouldShowPreviousButtonOnPage2() {
        pagination.clickNext();
        assertTrue(pagination.isPreviousButtonVisible(), "'« Anterior' should be visible on page 2");
        List<String> titlesPage2 = pagination.getArticleTitles();
        page.goBack();
        page.waitForLoadState();
        assertNotEquals(titlesPage2, pagination.getArticleTitles(), "Going back should show page 1 articles");
    }
}
