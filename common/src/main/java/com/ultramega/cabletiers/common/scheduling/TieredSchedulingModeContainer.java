package com.ultramega.cabletiers.common.scheduling;

import com.refinedmods.refinedstorage.common.support.SchedulingModeType;

import java.util.Collections;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;

/** Keeps Refined Storage 2.0.0 scheduling mode IDs and NBT keys. */
public class TieredSchedulingModeContainer {
    private static final String TAG_SCHEDULING_MODE = "sm";
    private static final String TAG_ROUND_ROBIN_INDEX = "rri";

    private final Consumer<TieredSchedulingMode> listener;
    private SchedulingModeType type = SchedulingModeType.DEFAULT;
    private TieredSchedulingMode schedulingMode;

    public TieredSchedulingModeContainer(final Consumer<TieredSchedulingMode> listener) {
        this.listener = listener;
        this.schedulingMode = createSchedulingMode(null);
        notifyListener();
    }

    public SchedulingModeType getType() {
        return type;
    }

    public void setType(final SchedulingModeType type) {
        setType(null, type);
    }

    private void setType(@Nullable final CompoundTag tag, final SchedulingModeType newType) {
        this.type = newType;
        this.schedulingMode = createSchedulingMode(tag);
        notifyListener();
    }

    private TieredSchedulingMode createSchedulingMode(@Nullable final CompoundTag tag) {
        return switch (type) {
            case DEFAULT -> new DefaultSchedulingMode();
            case RANDOM -> new RandomSchedulingMode(tasks -> Collections.shuffle(tasks, new Random()));
            case ROUND_ROBIN -> new RoundRobinSchedulingMode(new RoundRobinSchedulingMode.State(
                this::notifyListener,
                tag != null ? tag.getInt(TAG_ROUND_ROBIN_INDEX) : 0
            ));
        };
    }

    private void notifyListener() {
        listener.accept(schedulingMode);
    }

    public void loadFromTag(final CompoundTag tag) {
        if (tag.contains(TAG_SCHEDULING_MODE)) {
            setType(tag, SchedulingModeType.getById(tag.getInt(TAG_SCHEDULING_MODE)));
        }
    }

    public void writeToTag(final CompoundTag tag) {
        tag.putInt(TAG_SCHEDULING_MODE, type.getId());
        if (schedulingMode instanceof RoundRobinSchedulingMode roundRobin) {
            tag.putInt(TAG_ROUND_ROBIN_INDEX, roundRobin.getIndex());
        }
    }
}
