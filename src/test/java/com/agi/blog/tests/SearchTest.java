package com.agi.blog.tests;

import com.agi.blog.base.BaseTest;
import com.agi.blog.pages.HomePage;
import com.agi.blog.pages.SearchPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Blog do Agi")
@Feature("Busca de Artigos")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SearchTest extends BaseTest {

    private SearchPage searchPage;

    @BeforeEach
    void setup() {
        navigateToHome();
        new HomePage(page).clickSearchIcon();
        searchPage = new SearchPage(page);
    }

    @Test @Order(1)
    @Story("Busca com resultado") @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Busca com termo válido exibe ao menos um resultado")
    void shouldReturnResultsForValidTerm() {
        searchPage.typeSearchTerm("empréstimo");
        searchPage.submitByEnter();
        assertTrue(searchPage.hasResults(), "Search for 'empréstimo' should return results");
    }

    @Test @Order(2)
    @Story("Busca com resultado") @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Resultados exibem título clicável e link")
    void shouldDisplayResultsWithTitleAndLink() {
        searchPage.typeSearchTerm("poupança");
        searchPage.submitByEnter();
        assertTrue(searchPage.hasResults(), "Should have results");
        assertTrue(searchPage.allResultsHaveLinks(), "All results should have clickable links");
        assertFalse(searchPage.getFirstResultTitle().isEmpty(), "First result title should not be empty");
    }

    @Test @Order(3)
    @Story("Busca sem resultado") @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Busca com termo inválido exibe mensagem de nenhum resultado")
    void shouldShowNoResultsMessageForInvalidTerm() {
        searchPage.typeSearchTerm("xyzabc123invalido987");
        searchPage.submitByEnter();
        assertFalse(searchPage.hasResults(), "Should return no results for nonsense term");
        assertTrue(searchPage.isNoResultsMessageVisible(), "Should display no-results message");
    }

    @Test @Order(4)
    @Story("Variação de interação") @Severity(SeverityLevel.NORMAL)
    @DisplayName("Submeter com Enter retorna o mesmo número de resultados que o botão")
    void shouldReturnSameResultsWithEnterAndButton() {
        searchPage.typeSearchTerm("financiamento");
        searchPage.submitByEnter();
        int viaEnter = searchPage.getResultsCount();

        navigateToHome();
        new HomePage(page).clickSearchIcon();
        searchPage = new SearchPage(page);
        searchPage.typeSearchTerm("financiamento");
        searchPage.submitByButton();
        int viaButton = searchPage.getResultsCount();

        assertEquals(viaEnter, viaButton, "Enter and button should return same result count");
    }

    @Test @Order(5)
    @Story("Robustez") @Severity(SeverityLevel.MINOR)
    @DisplayName("Campo de busca aceita caracteres especiais sem quebrar a página")
    void shouldHandleSpecialCharactersWithoutBreaking() {
        searchPage.typeSearchTerm("@#$%&*!");
        searchPage.submitByEnter();
        assertTrue(page.url().contains("blog.agibank.com.br"), "Should stay on blog domain");
        assertFalse(page.title().isEmpty(), "Page title should not be empty");
    }

    @ParameterizedTest(name = "Busca por categoria: {0}")
    @Order(6)
    @ValueSource(strings = {"INSS", "Pix", "cartão"})
    @Story("Busca por categorias do blog") @Severity(SeverityLevel.NORMAL)
    @DisplayName("Busca por termos de categoria do blog retorna resultados")
    void shouldReturnResultsForBlogCategories(String term) {
        searchPage.typeSearchTerm(term);
        searchPage.submitByEnter();
        assertTrue(searchPage.hasResults(), "Search for '" + term + "' should return results");
    }
}
