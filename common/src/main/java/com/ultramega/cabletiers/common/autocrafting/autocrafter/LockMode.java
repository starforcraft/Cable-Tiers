package com.ultramega.cabletiers.common.autocrafting.autocrafter;

/**
 * Exact copy of {@link com.refinedmods.refinedstorage.common.autocrafting.autocrafter.LockMode}
 */
public enum LockMode {
    NEVER,
    LOCK_UNTIL_REDSTONE_PULSE_RECEIVED,
    LOCK_UNTIL_CONNECTED_MACHINE_IS_EMPTY,
    LOCK_UNTIL_ALL_OUTPUTS_ARE_RECEIVED,
    LOCK_UNTIL_HIGH_REDSTONE_SIGNAL,
    LOCK_UNTIL_LOW_REDSTONE_SIGNAL;

    LockMode toggle() {
        return switch (this) {
            case NEVER -> LOCK_UNTIL_REDSTONE_PULSE_RECEIVED;
            case LOCK_UNTIL_REDSTONE_PULSE_RECEIVED -> LOCK_UNTIL_CONNECTED_MACHINE_IS_EMPTY;
            case LOCK_UNTIL_CONNECTED_MACHINE_IS_EMPTY -> LOCK_UNTIL_ALL_OUTPUTS_ARE_RECEIVED;
            case LOCK_UNTIL_ALL_OUTPUTS_ARE_RECEIVED -> LOCK_UNTIL_HIGH_REDSTONE_SIGNAL;
            case LOCK_UNTIL_HIGH_REDSTONE_SIGNAL -> LOCK_UNTIL_LOW_REDSTONE_SIGNAL;
            case LOCK_UNTIL_LOW_REDSTONE_SIGNAL -> NEVER;
        };
    }
}
