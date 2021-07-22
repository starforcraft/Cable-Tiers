package com.YTrollman.CableTiers;

public enum CableTier {

    ELITE("elite", 2, 18),
    ULTRA("ultra", 6, 36),
    CREATIVE("creative", -1, 54);

    public static final CableTier[] VALUES = values();

    private final String name;
    private final int defaultSpeedMultiplier;
    private final int slots;

    CableTier(String name, int defaultSpeedMultiplier, int slots) {
        this.name = name;
        this.defaultSpeedMultiplier = defaultSpeedMultiplier;
        this.slots = slots;
    }

    public String getName() {
        return name;
    }

    public int getDefaultSpeedMultiplier() {
        return defaultSpeedMultiplier;
    }

    public int getSlots() {
        return slots;
    }
}
