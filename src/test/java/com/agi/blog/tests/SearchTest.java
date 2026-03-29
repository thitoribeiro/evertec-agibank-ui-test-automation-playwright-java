package com.agi.blog.tests;

import com.agi.blog.base.BaseTest;
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
        searchPage = new SearchPage(page);
    }

    @Test @Order(1)
    @Story("Busca com resultado") @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Busca com termo válido exibe ao menos um resultado")
    void shouldReturnResultsForValidTerm() {
        searchPage.searchFor(BASE_URL, "empréstimo");
        assertTrue(searchPage.hasResults(), "Search for 'empréstimo' should return results");
    }

    @Test @Order(2)
    @Story("Busca com resultado") @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Resultados exibem título clicável e link")
    void shouldDisplayResultsWithTitleAndLink() {
        searchPage.searchFor(BASE_URL, "poupança");
        assertTrue(searchPage.hasResults(), "Should have results");
        assertTrue(searchPage.allResultsHaveLinks(), "All results should have clickable links");
        assertFalse(searchPage.getFirstResultTitle().isEmpty(), "First result title should not be empty");
    }

    @Test @Order(3)
    @Story("Busca sem resultado") @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Busca com termo inválido exibe mensagem de nenhum resultado")
    void shouldShowNoResultsMessageForInvalidTerm() {
        searchPage.searchFor(BASE_URL, "xyzabc123invalido987");
        assertFalse(searchPage.hasResults(), "Should return no results for nonsense term");
        assertTrue(searchPage.isNoResultsMessageVisible(), "Should display no-results message");
    }

    @Test @Order(4)
    @Story("Variação de interação") @Severity(SeverityLevel.NORMAL)
    @DisplayName("Busca por URL retorna resultados consistentes em duas execuções")
    void shouldReturnConsistentResultsForSameTerm() {
        searchPage.searchFor(BASE_URL, "financiamento");
        int firstRun = searchPage.getResultsCount();

        searchPage.searchFor(BASE_URL, "financiamento");
        int secondRun = searchPage.getResultsCount();

        assertEquals(firstRun, secondRun, "Same search term should return same result count");
        assertTrue(firstRun > 0, "Should have results for 'financiamento'");
    }

    @Test @Order(5)
    @Story("Robustez") @Severity(SeverityLevel.MINOR)
    @DisplayName("Campo de busca aceita caracteres especiais sem quebrar a página")
    void shouldHandleSpecialCharactersWithoutBreaking() {
        searchPage.searchFor(BASE_URL, "@#$%&*!");
        assertTrue(page.url().contains("blog.agibank.com.br"), "Should stay on blog domain");
        assertFalse(page.title().isEmpty(), "Page title should not be empty");
    }

    @ParameterizedTest(name = "Busca por categoria: {0}")
    @Order(6)
    @ValueSource(strings = {"INSS", "Pix", "cartão"})
    @Story("Busca por categorias do blog") @Severity(SeverityLevel.NORMAL)
    @DisplayName("Busca por termos de categoria do blog retorna resultados")
    void shouldReturnResultsForBlogCategories(String term) {
        searchPage.searchFor(BASE_URL, term);
        assertTrue(searchPage.hasResults(), "Search for '" + term + "' should return results");
    }
}
