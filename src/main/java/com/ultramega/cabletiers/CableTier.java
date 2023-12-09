package com.ultramega.cabletiers;

public enum CableTier {
    ELITE("elite", 2, 2),
    ULTRA("ultra", 5, 4),
    MEGA("mega", 6.5, 6);

    public static final CableTier[] VALUES = values();

    private final String name;
    private final double defaultSpeedMultiplier;
    private final int slotsMultiplier;

    CableTier(String name, double defaultSpeedMultiplier, int slotsMultiplier) {
        this.name                   = name;
        this.defaultSpeedMultiplier = defaultSpeedMultiplier;
        this.slotsMultiplier        = slotsMultiplier;
    }

    public String getName() {
        return name;
    }

    public double getDefaultSpeedMultiplier() {
        return defaultSpeedMultiplier;
    }

    public int getSlotsMultiplier() {
        return slotsMultiplier;
    }
}
