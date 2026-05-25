package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.ultramega.cabletiers.common.TestResource;
import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputPatternState;
import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputRouting;
import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedResourceAmount;

import com.refinedmods.refinedstorage.api.autocrafting.task.ExternalPatternSink.Result;
import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.autocrafting.PlatformPatternProviderExternalPatternSink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;


import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TieredAutocrafterBlockEntityTest {
    @Test
    void shouldNotDuplicateSameResourceWhenItIsConfiguredInMultipleSidedSlots() {
        final TestSinks sinks = new TestSinks();

        final SidedInputPatternState sidedInputState = new SidedInputPatternState(List.of(
            Optional.of(sided(TestResource.A, 1, Direction.UP)),
            Optional.of(sided(TestResource.A, 1, Direction.NORTH))
        ));

        final Result result = SidedInputRouting.findResult(
            sinks.asArray(),
            Direction.WEST,
            List.of(amount(TestResource.A, 2)),
            Action.EXECUTE,
            (action, insertionResult) -> { },
            sidedInputState
        );

        assertThat(result).isEqualTo(Result.ACCEPTED);
        assertThat(sinks.amountInserted(Direction.UP, TestResource.A)).isEqualTo(1);
        assertThat(sinks.amountInserted(Direction.NORTH, TestResource.A)).isEqualTo(1);

        // EAST is the fallback direction.
        assertThat(sinks.amountInserted(Direction.EAST, TestResource.A)).isZero();

        assertThat(sinks.totalAmountInserted(TestResource.A)).isEqualTo(2);
    }

    @Test
    void shouldNotDuplicateAnySideResourceInFallbackPass() {
        final TestSinks sinks = new TestSinks();

        final SidedInputPatternState sidedInputState = new SidedInputPatternState(List.of(
            Optional.of(any(TestResource.A, 1)),
            Optional.of(sided(TestResource.A, 1, Direction.NORTH))
        ));

        final Result result = SidedInputRouting.findResult(
            sinks.asArray(),
            Direction.WEST,
            List.of(amount(TestResource.A, 2)),
            Action.EXECUTE,
            (action, insertionResult) -> { },
            sidedInputState
        );

        assertThat(result).isEqualTo(Result.ACCEPTED);

        // Any-side entry should go to fallback, which is EAST.
        assertThat(sinks.amountInserted(Direction.EAST, TestResource.A)).isEqualTo(1);
        assertThat(sinks.amountInserted(Direction.NORTH, TestResource.A)).isEqualTo(1);

        // The old logic inserted 1 + 1 + the combined fallback 2 = 4.
        assertThat(sinks.totalAmountInserted(TestResource.A)).isEqualTo(2);
    }

    @Test
    void shouldOnlyFallbackWithUnconsumedRemainder() {
        final TestSinks sinks = new TestSinks();

        final SidedInputPatternState sidedInputState = new SidedInputPatternState(List.of(
            Optional.of(sided(TestResource.A, 1, Direction.UP))
        ));

        final Result result = SidedInputRouting.findResult(
            sinks.asArray(),
            Direction.WEST,
            List.of(amount(TestResource.A, 2)),
            Action.EXECUTE,
            (action, insertionResult) -> { },
            sidedInputState
        );

        assertThat(result).isEqualTo(Result.ACCEPTED);
        assertThat(sinks.amountInserted(Direction.UP, TestResource.A)).isEqualTo(1);
        assertThat(sinks.amountInserted(Direction.EAST, TestResource.A)).isEqualTo(1);
        assertThat(sinks.totalAmountInserted(TestResource.A)).isEqualTo(2);
    }

    @Test
    void shouldMatchResourcesWhenFlatResourcesContainDuplicateEntries() {
        final boolean matches = SidedInputRouting.resourcesMatchIgnoringIndex(
            List.of(
                sided(TestResource.A, 1, Direction.UP),
                sided(TestResource.A, 1, Direction.NORTH),
                sided(TestResource.B, 1, Direction.SOUTH)
            ),
            List.of(
                amount(TestResource.A, 1),
                amount(TestResource.B, 1),
                amount(TestResource.A, 1)
            )
        );

        assertThat(matches).isTrue();
    }

    @Test
    void shouldNotMatchResourcesWithDifferentAmounts() {
        final boolean matches = SidedInputRouting.resourcesMatchIgnoringIndex(
            List.of(
                sided(TestResource.A, 1, Direction.UP),
                sided(TestResource.A, 1, Direction.NORTH)
            ),
            List.of(amount(TestResource.A, 3))
        );

        assertThat(matches).isFalse();
    }

    private static ResourceAmount amount(final ResourceKey resource, final long amount) {
        return new ResourceAmount(resource, amount);
    }

    private static SidedResourceAmount sided(final ResourceKey resource, final long amount, final Direction direction) {
        return new SidedResourceAmount(amount(resource, amount), Optional.of(direction));
    }

    private static SidedResourceAmount any(final ResourceKey resource, final long amount) {
        return new SidedResourceAmount(amount(resource, amount), Optional.empty());
    }

    private static final class TestSinks {
        private final EnumMap<Direction, RecordingSink> sinks = new EnumMap<>(Direction.class);

        private TestSinks() {
            for (final Direction direction : Direction.values()) {
                this.sinks.put(direction, new RecordingSink());
            }
        }

        private PlatformPatternProviderExternalPatternSink[] asArray() {
            final PlatformPatternProviderExternalPatternSink[] result = new PlatformPatternProviderExternalPatternSink[Direction.values().length];
            for (final Direction direction : Direction.values()) {
                result[direction.ordinal()] = this.sinks.get(direction);
            }
            return result;
        }

        private long amountInserted(final Direction direction, final ResourceKey resource) {
            return this.sinks.get(direction).amountInserted(resource);
        }

        private long totalAmountInserted(final ResourceKey resource) {
            return this.sinks.values().stream()
                .mapToLong(sink -> sink.amountInserted(resource))
                .sum();
        }
    }

    private static final class RecordingSink implements PlatformPatternProviderExternalPatternSink {
        private final List<ResourceAmount> inserted = new ArrayList<>();

        @Override
        public Result insertAll(final Collection<ResourceAmount> resources, final Action action) {
            this.inserted.addAll(resources);
            return Result.ACCEPTED;
        }

        @Override
        public boolean isEmpty() {
            return this.inserted.isEmpty();
        }

        private long amountInserted(final ResourceKey resource) {
            return this.inserted.stream()
                .filter(resourceAmount -> resourceAmount.resource().equals(resource))
                .mapToLong(ResourceAmount::amount)
                .sum();
        }
    }
}
