package com.ultramega.cabletiers.common;

import java.util.EnumMap;
import java.util.Map;

public final class DefaultConfig {
    private static final Map<CableTiers, Map<CableType, Long>> USAGE_MAP = new EnumMap<>(CableTiers.class);
    private static final Map<CableTiers, Map<CableType, Integer>> SPEED_MAP = new EnumMap<>(CableTiers.class);
    private static final Map<CableTiers, Map<CableType, Boolean>> STACK_UPGRADE_MAP = new EnumMap<>(CableTiers.class);

    static {
        for (final CableTiers tier : CableTiers.values()) {
            USAGE_MAP.put(tier, new EnumMap<>(CableType.class));
            SPEED_MAP.put(tier, new EnumMap<>(CableType.class));
            STACK_UPGRADE_MAP.put(tier, new EnumMap<>(CableType.class));
        }

        // energy values
        USAGE_MAP.get(CableTiers.ELITE).put(CableType.IMPORTER, 4L);
        USAGE_MAP.get(CableTiers.ELITE).put(CableType.EXPORTER, 4L);
        USAGE_MAP.get(CableTiers.ELITE).put(CableType.DESTRUCTOR, 5L);
        USAGE_MAP.get(CableTiers.ELITE).put(CableType.CONSTRUCTOR, 5L);
        USAGE_MAP.get(CableTiers.ELITE).put(CableType.DISK_INTERFACE, 6L);

        USAGE_MAP.get(CableTiers.ULTRA).put(CableType.IMPORTER, 8L);
        USAGE_MAP.get(CableTiers.ULTRA).put(CableType.EXPORTER, 8L);
        USAGE_MAP.get(CableTiers.ULTRA).put(CableType.DESTRUCTOR, 9L);
        USAGE_MAP.get(CableTiers.ULTRA).put(CableType.CONSTRUCTOR, 9L);
        USAGE_MAP.get(CableTiers.ULTRA).put(CableType.DISK_INTERFACE, 10L);

        USAGE_MAP.get(CableTiers.MEGA).put(CableType.IMPORTER, 16L);
        USAGE_MAP.get(CableTiers.MEGA).put(CableType.EXPORTER, 16L);
        USAGE_MAP.get(CableTiers.MEGA).put(CableType.DESTRUCTOR, 17L);
        USAGE_MAP.get(CableTiers.MEGA).put(CableType.CONSTRUCTOR, 17L);
        USAGE_MAP.get(CableTiers.MEGA).put(CableType.DISK_INTERFACE, 18L);

        // speed values
        SPEED_MAP.get(CableTiers.ELITE).put(CableType.IMPORTER, 2);
        SPEED_MAP.get(CableTiers.ELITE).put(CableType.EXPORTER, 2);
        SPEED_MAP.get(CableTiers.ELITE).put(CableType.DESTRUCTOR, 2);
        SPEED_MAP.get(CableTiers.ELITE).put(CableType.CONSTRUCTOR, 2);
        SPEED_MAP.get(CableTiers.ELITE).put(CableType.DISK_INTERFACE, 2000);

        SPEED_MAP.get(CableTiers.ULTRA).put(CableType.IMPORTER, 5);
        SPEED_MAP.get(CableTiers.ULTRA).put(CableType.EXPORTER, 5);
        SPEED_MAP.get(CableTiers.ULTRA).put(CableType.DESTRUCTOR, 5);
        SPEED_MAP.get(CableTiers.ULTRA).put(CableType.CONSTRUCTOR, 5);
        SPEED_MAP.get(CableTiers.ULTRA).put(CableType.DISK_INTERFACE, 5000);

        SPEED_MAP.get(CableTiers.MEGA).put(CableType.IMPORTER, 8);
        SPEED_MAP.get(CableTiers.MEGA).put(CableType.EXPORTER, 8);
        SPEED_MAP.get(CableTiers.MEGA).put(CableType.DESTRUCTOR, 8);
        SPEED_MAP.get(CableTiers.MEGA).put(CableType.CONSTRUCTOR, 8);
        SPEED_MAP.get(CableTiers.MEGA).put(CableType.DISK_INTERFACE, 8000);

        SPEED_MAP.get(CableTiers.CREATIVE).put(CableType.IMPORTER, 15000);
        SPEED_MAP.get(CableTiers.CREATIVE).put(CableType.EXPORTER, 15000);
        SPEED_MAP.get(CableTiers.CREATIVE).put(CableType.DESTRUCTOR, 15000);
        SPEED_MAP.get(CableTiers.CREATIVE).put(CableType.CONSTRUCTOR, 15000);
        SPEED_MAP.get(CableTiers.CREATIVE).put(CableType.DISK_INTERFACE, 750000);

        // stack upgrade values
        STACK_UPGRADE_MAP.get(CableTiers.ELITE).put(CableType.IMPORTER, false);
        STACK_UPGRADE_MAP.get(CableTiers.ELITE).put(CableType.EXPORTER, false);
        STACK_UPGRADE_MAP.get(CableTiers.ELITE).put(CableType.CONSTRUCTOR, false);
        STACK_UPGRADE_MAP.get(CableTiers.ELITE).put(CableType.DISK_INTERFACE, false);

        STACK_UPGRADE_MAP.get(CableTiers.ULTRA).put(CableType.IMPORTER, true);
        STACK_UPGRADE_MAP.get(CableTiers.ULTRA).put(CableType.EXPORTER, true);
        STACK_UPGRADE_MAP.get(CableTiers.ULTRA).put(CableType.CONSTRUCTOR, true);
        STACK_UPGRADE_MAP.get(CableTiers.ULTRA).put(CableType.DISK_INTERFACE, true);

        STACK_UPGRADE_MAP.get(CableTiers.MEGA).put(CableType.IMPORTER, true);
        STACK_UPGRADE_MAP.get(CableTiers.MEGA).put(CableType.EXPORTER, true);
        STACK_UPGRADE_MAP.get(CableTiers.MEGA).put(CableType.CONSTRUCTOR, true);
        STACK_UPGRADE_MAP.get(CableTiers.MEGA).put(CableType.DISK_INTERFACE, true);
    }

    private DefaultConfig() {
    }

    public static long getUsageFor(final CableTiers tier, final CableType type) {
        return USAGE_MAP.getOrDefault(tier, Map.of()).getOrDefault(type, 0L);
    }

    public static int getSpeedFor(final CableTiers tier, final CableType type) {
        return SPEED_MAP.getOrDefault(tier, Map.of()).getOrDefault(type, 0);
    }

    public static boolean isStackUpgradeIntegrated(final CableTiers tier, final CableType type) {
        return STACK_UPGRADE_MAP.getOrDefault(tier, Map.of()).getOrDefault(type, false);
    }
}
