package com.YTrollman.CableTiers;

public enum CableTier {

    ELITE("elite", 2, 2),
    ULTRA("ultra", 6, 4),
    CREATIVE("creative", -1, 6);

    public static final CableTier[] VALUES = values();

    private final String name;
    private final int defaultSpeedMultiplier;
    private final int slotsMultiplier;

    CableTier(String name, int defaultSpeedMultiplier, int slotsMultiplier) {
        this.name = name;
        this.defaultSpeedMultiplier = defaultSpeedMultiplier;
        this.slotsMultiplier = slotsMultiplier;
    }

    public String getName() {
        return name;
    }

    public int getDefaultSpeedMultiplier() {
        return defaultSpeedMultiplier;
    }

    public int getSlotsMultiplier() {
        return slotsMultiplier;
    }
}
