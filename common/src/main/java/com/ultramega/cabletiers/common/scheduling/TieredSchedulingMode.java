package com.ultramega.cabletiers.common.scheduling;

import com.refinedmods.refinedstorage.api.network.node.SchedulingMode;

import java.util.List;

/** A scheduling mode that reports whether another speed-loop pass can be useful. */
@FunctionalInterface
public interface TieredSchedulingMode {
    boolean execute(List<? extends SchedulingMode.ScheduledTask> tasks);
}
