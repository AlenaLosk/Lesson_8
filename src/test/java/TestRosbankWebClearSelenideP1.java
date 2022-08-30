import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.*;

@DisplayName("Check menu points and search result")
public class TestRosbankWebClearSelenideP1 {
    private final String mainPage = "https://www.rosbank.ru/";
    private final String logoLocator = ".Logo__image";
    private final String mainMenuItemsLocator = "span[class*='HeaderExtendableMenuV2__item']";
    private final String mainMenuSubItemsLocator = "[class*='dropdown-open'] span";
    private final String headerTitleLocator = ".ComponentKit__header .ComponentKit__title";
    private final String searchButtonLocator = "[class*='topbar-search']";
    private final String searchInputLocator = "[placeholder='Поиск по сайту']";
    private final String loadingBarLocator = ".rb-search__container .results__loading";
    private final String searchResultItemsLocator = "[class*='page_type_search'] yass-li a yass-span";

    @BeforeAll
    public static void beforeAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void setUp() {
        open(mainPage);
        $(logoLocator).shouldHave(Condition.attribute("alt", "Росбанк"));
    }

    @DisplayName("Check main menu points")
    @CsvSource(value = {"Карты, Дебетовые карты", "Карты, Кредитные карты", "Карты, Черти-что"})
    @ParameterizedTest(name = "Result of selecting tab \"{0}\" -> \"{1}\" is page with text \"{1}\"")
    public void fillFormTest(String param1, String param2) {
        $$(mainMenuItemsLocator).findBy(Condition.text(param1)).click();
        $$(mainMenuSubItemsLocator).findBy(Condition.text(param2)).click();
        $(headerTitleLocator).shouldHave(Condition.text(param2));
    }

    @DisplayName("Check search result")
    @CsvFileSource(resources = "/search_result.csv", useHeadersInDisplayName = true)
    @ParameterizedTest(name = "Result of searching \"{0}\" is page with text \"{1}\" and \"{2}\"")
    public void checkSearchResultTest(String param1, String param2, String param3) {
        $(searchButtonLocator).click();
        $(searchInputLocator).setValue(param1).pressEnter();
        while ($(loadingBarLocator).exists() || $$(searchResultItemsLocator).size() < 9) {
            sleep(300);
        }
        $$(searchResultItemsLocator)
                .should(CollectionCondition.anyMatch("Search results don't contain char sequence: " + param2,
                        e -> e.getText().toLowerCase(Locale.ROOT).contains(param2)))
                .should(CollectionCondition.anyMatch("Search results don't contain char sequence: " + param3,
                        e -> e.getText().toLowerCase(Locale.ROOT).contains(param3)));
    }

    @DisplayName("Check that search result isn't null")
    @ArgumentsSource(MyArgumentsProvider.class)
    @ParameterizedTest(name = "Result of searching \"{0}\" is not null")
    public void testWithArgumentsSource(String argument) {
        $(searchButtonLocator).click();
        $(searchInputLocator).setValue(argument).pressEnter();
        while ($(loadingBarLocator).exists()) {
            sleep(300);
        }
        $$(searchResultItemsLocator).should(CollectionCondition.sizeGreaterThan(1));
    }
}
