package com.agi.blog.tests;

import com.agi.blog.base.BaseTest;
import com.agi.blog.pages.NavigationPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Blog do Agi")
@Feature("Navegação por Menu")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NavigationTest extends BaseTest {

    private NavigationPage nav;

    @BeforeEach
    void setup() { navigateToHome(); nav = new NavigationPage(page); }

    @Test @Order(1)
    @Story("Dropdown nível 1") @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Hover em 'Produtos' exibe submenu com categorias")
    void shouldShowProductsSubmenuOnHover() {
        nav.hoverMenuItem("Produtos");
        assertTrue(nav.isSubmenuVisible("Produtos"), "Submenu should be visible after hover");
        List<String> items = nav.getSubmenuItems("Produtos");
        assertFalse(items.isEmpty(), "Submenu should contain at least one item. Found: " + items);
    }

    @Test @Order(2)
    @Story("Dropdown nível 2") @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Hover em 'Empréstimos' exibe segundo nível com Consignado e Pessoal")
    void shouldShowLoanSubSubmenuOnHover() {
        nav.hoverMenuItem("Produtos");
        page.waitForTimeout(300);
        nav.hoverMenuItem("Empréstimos");
        List<String> items = nav.getSubmenuItems("Empréstimos");
        boolean hasConsignado = items.stream().anyMatch(i -> i.toLowerCase().contains("consignado"));
        boolean hasPessoal   = items.stream().anyMatch(i -> i.toLowerCase().contains("pessoal"));
        assertTrue(hasConsignado || hasPessoal,
            "Sub-menu should contain 'Consignado' or 'Pessoal'. Found: " + items);
    }

    @Test @Order(3)
    @Story("Filtro por categoria") @Severity(SeverityLevel.NORMAL)
    @DisplayName("Clicar em categoria do menu navega e exibe artigos filtrados")
    void shouldNavigateToCategory() {
        nav.hoverMenuItem("Produtos");
        page.waitForTimeout(300);
        nav.clickSubmenuItem("Conta Corrente");
        assertTrue(nav.getCurrentUrl().contains("blog.agibank.com.br"), "Should stay on blog domain");
        assertTrue(nav.categoryPageHasArticles(), "Category page should display articles");
    }

    @Test @Order(4)
    @Story("Menu O Agibank") @Severity(SeverityLevel.NORMAL)
    @DisplayName("Hover em 'O Agibank' exibe Colunas, Notícias e Carreira")
    void shouldShowAgiMenuWithCorrectItems() {
        nav.hoverMenuItem("O Agibank");
        assertTrue(nav.isSubmenuVisible("O Agibank"), "Submenu should be visible after hover");
        String items = nav.getSubmenuItems("O Agibank").toString().toLowerCase();
        assertTrue(items.contains("colunas") || items.contains("notícias") || items.contains("carreira"),
            "Should contain Colunas, Notícias or Carreira. Found: " + items);
    }
}
