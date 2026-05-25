package com.ultramega.cabletiers.common.autocrafting.sidedinput;

import com.refinedmods.refinedstorage.api.autocrafting.task.ExternalPatternSink.Result;
import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.autocrafting.PlatformPatternProviderExternalPatternSink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public final class SidedInputRouting {
    private SidedInputRouting() {
    }

    public static Result findResult(final PlatformPatternProviderExternalPatternSink[] sinks,
                                    @Nullable final Direction baseDirection,
                                    final Collection<ResourceAmount> resources,
                                    final Action action,
                                    final BiConsumer<Action, Result> afterAccept,
                                    @Nullable final SidedInputPatternState sidedInputState) {
        final Direction fallbackDirection = (baseDirection != null) ? baseDirection.getOpposite() : null;

        if (sidedInputState != null) {
            final List<Result> results = new ArrayList<>();
            final Map<ResourceKey, Long> remainingResources = toAmountMap(resources);

            for (final Optional<SidedResourceAmount> optionalResource : sidedInputState.sidedResources()) {
                if (optionalResource.isEmpty()) {
                    continue;
                }

                final SidedResourceAmount sidedResource = optionalResource.get();
                final ResourceAmount resource = sidedResource.resource();
                final Direction targetDirection = sidedResource.inputDirection().orElse(fallbackDirection);
                if (targetDirection == null) {
                    continue;
                }

                final Result result = sinks[targetDirection.ordinal()].insertAll(Set.of(resource), action);
                afterAccept.accept(action, result);
                results.add(result);

                consumeResource(remainingResources, resource);
            }

            if (baseDirection != null) {
                for (final ResourceAmount resource : toResourceAmounts(remainingResources)) {
                    final Result result = sinks[baseDirection.getOpposite().ordinal()].insertAll(Set.of(resource), action);
                    afterAccept.accept(action, result);
                    results.add(result);
                }
            }

            if (!results.isEmpty()) {
                return getMostImportantResult(results);
            }
        }

        if (baseDirection == null) {
            return Result.REJECTED;
        }
        final Result result = sinks[baseDirection.getOpposite().ordinal()].insertAll(resources, action);
        afterAccept.accept(action, result);
        return result;
    }

    public static boolean resourcesMatchIgnoringIndex(final List<SidedResourceAmount> sidedResources,
                                                      final List<ResourceAmount> resources) {
        final Map<ResourceKey, Long> sidedMerged = sidedResources.stream()
            .collect(Collectors.groupingBy(
                sra -> sra.resource().resource(),
                Collectors.summingLong(sra -> sra.resource().amount())
            ));

        final Map<ResourceKey, Long> flatResources = toAmountMap(resources);
        if (!sidedMerged.keySet().equals(flatResources.keySet())) {
            return false;
        }

        for (final ResourceKey key : sidedMerged.keySet()) {
            if (!Objects.equals(sidedMerged.get(key), flatResources.get(key))) {
                return false;
            }
        }

        return true;
    }

    private static Map<ResourceKey, Long> toAmountMap(final Collection<ResourceAmount> resources) {
        return resources.stream()
            .collect(Collectors.groupingBy(
                ResourceAmount::resource,
                Collectors.summingLong(ResourceAmount::amount)
            ));
    }

    private static void consumeResource(final Map<ResourceKey, Long> remainingResources,
                                        final ResourceAmount resource) {
        remainingResources.computeIfPresent(
            resource.resource(),
            (key, amount) -> Math.max(0, amount - resource.amount())
        );
    }

    private static List<ResourceAmount> toResourceAmounts(final Map<ResourceKey, Long> resources) {
        return resources.entrySet().stream()
            .filter(entry -> entry.getValue() > 0)
            .map(entry -> new ResourceAmount(entry.getKey(), entry.getValue()))
            .toList();
    }

    public static Result getMostImportantResult(final List<Result> results) {
        final List<Result> priority = List.of(Result.REJECTED, Result.SKIPPED, Result.LOCKED);

        for (final Result result : priority) {
            if (results.contains(result)) {
                return result;
            }
        }

        // Fallback
        return results.getFirst();
    }
}
