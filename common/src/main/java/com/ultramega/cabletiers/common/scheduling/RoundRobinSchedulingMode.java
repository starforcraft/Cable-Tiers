package com.ultramega.cabletiers.common.scheduling;

import com.refinedmods.refinedstorage.api.network.node.SchedulingMode;

import java.util.List;

public class RoundRobinSchedulingMode implements TieredSchedulingMode {
    private final State state;

    public RoundRobinSchedulingMode(final State state) {
        this.state = state;
    }

    @Override
    public boolean execute(final List<? extends SchedulingMode.ScheduledTask> tasks) {
        if (tasks.isEmpty()) {
            return false;
        }
        final int startIndex = state.getIndex() % tasks.size();
        for (int i = startIndex; i < tasks.size(); ++i) {
            if (tasks.get(i).run()) {
                state.setIndex((state.getIndex() + 1) % tasks.size());
                return true;
            }
        }
        state.setIndex(0);
        // Upstream round-robin only visits [startIndex, size) in one pass.
        // A pass resetting the index to zero must not prevent the next pass
        // from trying [0, startIndex), where work might still succeed.
        return startIndex != 0;
    }

    public int getIndex() {
        return state.getIndex();
    }

    public static class State {
        private final Runnable callback;
        private int index;

        public State(final Runnable callback, final int index) {
            this.callback = callback;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(final int index) {
            final boolean didChange = this.index != index;
            this.index = index;
            if (didChange) {
                callback.run();
            }
        }
    }
}
