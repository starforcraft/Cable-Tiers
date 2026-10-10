package com.ultramega.cabletiers.common.scheduling;

import com.refinedmods.refinedstorage.common.support.SchedulingModeType;

import java.util.Collections;
import java.util.Random;
import java.util.function.Consumer;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Nearly exact copy of {@link com.refinedmods.refinedstorage.common.support.SchedulingModeContainer}
 * with stuff from {@link com.refinedmods.refinedstorage.common.support.SchedulingModeType}
 */
public class TieredSchedulingModeContainer {
    private static final String TAG_SCHEDULING_MODE = "sm";
    private static final String TAG_ROUND_ROBIN_INDEX = "rri";

    private final Consumer<TieredSchedulingMode> listener;
    private final Runnable changeListener;

    private SchedulingModeType type = SchedulingModeType.DEFAULT;
    private TieredSchedulingMode schedulingMode;

    public TieredSchedulingModeContainer(final Consumer<TieredSchedulingMode> listener,
                                         final Runnable changeListener) {
        this.listener = listener;
        this.changeListener = changeListener;
        this.schedulingMode = this.createSchedulingMode(null);
        this.notifyListeners(false);
    }

    public SchedulingModeType getType() {
        return this.type;
    }

    public void setType(final SchedulingModeType type) {
        this.setType(null, type, true);
    }

    private void setType(@Nullable final ValueInput input,
                         final SchedulingModeType newType,
                         final boolean changed) {
        this.type = newType;
        this.schedulingMode = this.createSchedulingMode(input);
        this.notifyListeners(changed);
    }

    private TieredSchedulingMode createSchedulingMode(@Nullable final ValueInput input) {
        return switch (this.type) {
            case DEFAULT -> new DefaultSchedulingMode();
            case RANDOM -> new RandomSchedulingMode(tasks -> Collections.shuffle(tasks, new Random()));
            case ROUND_ROBIN -> new RoundRobinSchedulingMode(new RoundRobinSchedulingMode.State(
                this.changeListener,
                input != null ? input.getIntOr(TAG_ROUND_ROBIN_INDEX, 0) : 0
            ));
        };
    }

    private void notifyListeners(final boolean changed) {
        this.listener.accept(this.schedulingMode);
        if (changed) {
            this.changeListener.run();
        }
    }

    public void read(final ValueInput input) {
        input.getInt(TAG_SCHEDULING_MODE).map(SchedulingModeType::getById)
            .ifPresent(mode -> this.setType(input, mode, false));
    }

    public void store(final ValueOutput output) {
        output.putInt(TAG_SCHEDULING_MODE, this.type.getId());
        if (this.schedulingMode instanceof RoundRobinSchedulingMode roundRobin) {
            output.putInt(TAG_ROUND_ROBIN_INDEX, roundRobin.getIndex());
        }
    }
}
