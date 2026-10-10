package com.ultramega.cabletiers.common.scheduling;

import com.refinedmods.refinedstorage.api.network.node.SchedulingMode;
import com.refinedmods.refinedstorage.api.network.node.SchedulingMode.ScheduledTask;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TieredSchedulingModeTest {
    @Test
    void defaultStopsAfterSuccessfulTask() {
        final AtomicInteger attempts = new AtomicInteger();
        final List<ScheduledTask> tasks = List.of(
            () -> { attempts.incrementAndGet(); return false; },
            () -> { attempts.incrementAndGet(); return true; },
            () -> { attempts.incrementAndGet(); return true; }
        );

        assertThat(new DefaultSchedulingMode().execute(tasks)).isTrue();
        assertThat(attempts).hasValue(2);
    }

    @Test
    void defaultReturnsFalseAfterTryingAllTasks() {
        final AtomicInteger attempts = new AtomicInteger();
        final ScheduledTask task = () -> { attempts.incrementAndGet(); return false; };

        assertThat(new DefaultSchedulingMode().execute(List.of(task, task))).isFalse();
        assertThat(attempts).hasValue(2);
    }

    @Test
    void randomTriesEachTaskUntilSuccess() {
        final AtomicInteger attempts = new AtomicInteger();
        final RandomSchedulingMode mode = new RandomSchedulingMode(Collections::reverse);
        final List<ScheduledTask> tasks = List.of(
            () -> { attempts.addAndGet(100); return true; },
            () -> { attempts.incrementAndGet(); return false; }
        );

        assertThat(mode.execute(tasks)).isTrue();
        assertThat(attempts).hasValue(101);
        assertThat(mode.execute(List.of(() -> false))).isFalse();
        assertThat(mode.execute(List.of())).isFalse();
    }

    @Test
    void roundRobinAllowsAnotherPassAfterResettingItsIndex() {
        final AtomicInteger changes = new AtomicInteger();
        final AtomicInteger firstTaskRuns = new AtomicInteger();
        final RoundRobinSchedulingMode mode = new RoundRobinSchedulingMode(new RoundRobinSchedulingMode.State(changes::incrementAndGet, 2));
        final List<ScheduledTask> tasks = List.of(
            () -> { firstTaskRuns.incrementAndGet(); return true; },
            () -> false,
            () -> false
        );

        // A failed tail scan does not imply that earlier tasks cannot succeed.
        assertThat(mode.execute(tasks)).isTrue();
        assertThat(mode.getIndex()).isZero();
        assertThat(firstTaskRuns).hasValue(0);
        assertThat(mode.execute(tasks)).isTrue();
        assertThat(firstTaskRuns).hasValue(1);
        assertThat(mode.getIndex()).isEqualTo(1);
        assertThat(changes).hasValue(2);
    }

    @Test
    void idleRoundRobinNeedsAtMostTwoPasses() {
        final AtomicInteger attempts = new AtomicInteger();
        final RoundRobinSchedulingMode mode = new RoundRobinSchedulingMode(new RoundRobinSchedulingMode.State(() -> {}, 1));
        final List<ScheduledTask> tasks = List.of(
            () -> { attempts.incrementAndGet(); return false; },
            () -> { attempts.incrementAndGet(); return false; }
        );

        assertThat(mode.execute(tasks)).isTrue(); // Reset to index 0
        assertThat(mode.execute(tasks)).isFalse(); // Scanned all tasks
        assertThat(attempts).hasValue(3);
        assertThat(mode.getIndex()).isZero();
    }
}
