package com.ultramega.cabletiers.common.scheduling;

import com.refinedmods.refinedstorage.api.network.node.SchedulingMode;

import java.util.List;

public class DefaultSchedulingMode implements TieredSchedulingMode {
    @Override
    public boolean execute(final List<? extends SchedulingMode.ScheduledTask> tasks) {
        for (final SchedulingMode.ScheduledTask task : tasks) {
            if (task.run()) {
                return true;
            }
        }
        return false;
    }
}
