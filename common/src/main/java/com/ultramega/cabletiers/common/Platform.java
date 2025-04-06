package com.ultramega.cabletiers.common;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public final class Platform {
    @Nullable
    private static Supplier<Config> configProvider = null;

    private Platform() {
    }

    public static void setConfigProvider(final Supplier<Config> configProvider) {
        Platform.configProvider = configProvider;
    }

    public static Config getConfig() {
        return requireNonNull(configProvider, "Config isn't loaded yet").get();
    }
}
