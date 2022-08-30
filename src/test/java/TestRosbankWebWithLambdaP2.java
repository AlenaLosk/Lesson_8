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
import static io.qameta.allure.Allure.step;

@DisplayName("Check menu points and search result")
public class TestRosbankWebWithLambdaP2 {

    @BeforeAll
    public static void beforeAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void setUp() {
        step("Открываем главную страницу", () -> open("https://www.rosbank.ru/"));
        step("Проверяем наличие лого", () -> $(".Logo__image").shouldHave(Condition.attribute("alt", "Росбанк")));
    }

    @DisplayName("Check main menu points")
    @CsvSource(value = {"Карты, Дебетовые карты", "Карты, Кредитные карты", "Карты, Черти-что"})
    @ParameterizedTest(name = "Result of selecting tab \"{0}\" -> \"{1}\" is page with text \"{1}\"")
    public void fillFormTest(String param1, String param2) {
        step("Кликаем в главном меню на '" + param1 + "'",
                () -> $$("span[class*='HeaderExtendableMenuV2__item']").findBy(Condition.text(param1)).click());
        step("Кликаем в подпункте меню на '" + param2 + "'",
                () -> $$("[class*='dropdown-open'] span").findBy(Condition.text(param2)).click());
        step("Проверяем, что на открывшейся странице есть текст '" + param2 + "'",
                () -> $(".ComponentKit__header .ComponentKit__title").shouldHave(Condition.text(param2)));
    }

    @DisplayName("Check search result")
    @CsvFileSource(resources = "/search_result.csv", useHeadersInDisplayName = true)
    @ParameterizedTest(name = "Result of searching \"{0}\" is page with text \"{1}\" and \"{2}\"")
    public void checkSearchResultTest(String param1, String param2, String param3) {
        step("Кликаем на кнопке поиска (лупа)",
                () -> $("[class*='topbar-search']").click());
        step("В строке поиска вводим '" + param1 + "'",
                () -> $("[placeholder='Поиск по сайту']").setValue(param1).pressEnter());
        step("Ждем прогрузки результатов поиска",
                () ->
                {
                    while ($(".rb-search__container .results__loading").exists()
                            || $$("[class*='page_type_search'] yass-li a yass-span").size() < 9) {
                        sleep(300);
                    }
                }
        );
        step("В результатах поиска проверяем наличие строк с текстом '" + param2 + "' и '" + param3 + "'",
                () -> $$("[class*='page_type_search'] yass-li a yass-span")
                        .should(CollectionCondition.anyMatch("Search results don't contain char sequence: " + param2,
                                e -> e.getText().toLowerCase(Locale.ROOT).contains(param2)))
                        .should(CollectionCondition.anyMatch("Search results don't contain char sequence: " + param3,
                                e -> e.getText().toLowerCase(Locale.ROOT).contains(param3))));
    }

    @DisplayName("Check that search result isn't null")
    @ArgumentsSource(MyArgumentsProvider.class)
    @ParameterizedTest(name = "Result of searching \"{0}\" is not null")
    public void testWithArgumentsSource(String argument) {
        step("Кликаем на кнопке поиска (лупа)",
                () -> $("[class*='topbar-search']").click());
        step("В строке поиска вводим '" + argument + "'",
                () -> $("[placeholder='Поиск по сайту']").setValue(argument).pressEnter());
        step("Ждем прогрузки результатов поиска",
                () ->
                {
                    while ($(".rb-search__container .results__loading").exists()) {
                        sleep(300);
                    }
                }
        );
        step("Проверяем, что результатом поиска явлется более, чем 1 строка",
                () -> $$("[class*='page_type_search'] yass-li a yass-span")
                .should(CollectionCondition.sizeGreaterThan(1)));
    }
}
