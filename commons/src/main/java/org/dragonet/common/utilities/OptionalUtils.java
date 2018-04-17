package org.dragonet.common.utilities;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class OptionalUtils {

    // Utility class
    private OptionalUtils() {
    }

    public static <T, R> R handle(Optional<T> optional, Function<T, R> present, Supplier<R> notPresent) {
        if (optional.isPresent()) {
            return present.apply(optional.get());
        } else {
            return notPresent.get();
        }
    }

}
