package com.ultramega.cabletiers.common.scheduling;

import com.refinedmods.refinedstorage.api.network.node.SchedulingMode.ScheduledTask;

import java.util.List;

/** Like {@link com.refinedmods.refinedstorage.api.network.node.SchedulingMode} but returns boolean to report whether another speed-loop pass can be useful */
@FunctionalInterface
public interface TieredSchedulingMode {
    boolean execute(List<? extends ScheduledTask> tasks);
}
