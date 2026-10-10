package com.ultramega.cabletiers.common.scheduling;

import com.refinedmods.refinedstorage.api.network.node.SchedulingMode;

import java.util.ArrayList;
import java.util.List;

public class RandomSchedulingMode implements TieredSchedulingMode {
    private final Randomizer randomizer;

    public RandomSchedulingMode(final Randomizer randomizer) {
        this.randomizer = randomizer;
    }

    @Override
    public boolean execute(final List<? extends SchedulingMode.ScheduledTask> tasks) {
        if (tasks.isEmpty()) {
            return false;
        }
        final List<SchedulingMode.ScheduledTask> shuffledTasks = new ArrayList<>(tasks);
        randomizer.shuffle(shuffledTasks);
        for (final SchedulingMode.ScheduledTask task : shuffledTasks) {
            if (task.run()) {
                return true;
            }
        }
        return false;
    }

    @FunctionalInterface
    public interface Randomizer {
        void shuffle(List<SchedulingMode.ScheduledTask> tasks);
    }
}
