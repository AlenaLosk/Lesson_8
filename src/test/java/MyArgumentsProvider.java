import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class MyArgumentsProvider implements ArgumentsProvider {
    public MyArgumentsProvider() {
    }
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(Arguments.of("сбережения"), Arguments.of("инвестиции"));
    }
}
