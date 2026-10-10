package com.ultramega.cabletiers.common.scheduling;

import com.refinedmods.refinedstorage.api.network.node.SchedulingMode.ScheduledTask;

import java.util.List;

/**
 * Nearly exact copy of {@link com.refinedmods.refinedstorage.api.network.impl.node.task.DefaultSchedulingMode}
 */
public class DefaultSchedulingMode implements TieredSchedulingMode {
    @Override
    public boolean execute(final List<? extends ScheduledTask> tasks) {
        for (final ScheduledTask task : tasks) {
            if (task.run()) {
                return true;
            }
        }
        return false;
    }
}
