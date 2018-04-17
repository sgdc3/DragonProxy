package org.dragonet.common.utilities;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class OptionalUtils {

    // Utility class
    private OptionalUtils() {
    }

    public static <T, R> R handle(Optional<T> optional, Function<T, R> present, Supplier<R> notPresent) {
        return optional.isPresent() ? present.apply(optional.get()) : notPresent.get();
    }

    public static <T> void ifPresentOr(Optional<T> optional, Consumer<T> present, Runnable notPresent) {
        if (optional.isPresent()) {
            present.accept(optional.get());
        } else {
            notPresent.run();
        }
    }

}
