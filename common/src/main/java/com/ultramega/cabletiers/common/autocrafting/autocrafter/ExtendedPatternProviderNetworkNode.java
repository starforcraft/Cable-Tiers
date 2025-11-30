package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.ultramega.cabletiers.common.mixin.InvokerAbstractTaskPattern;
import com.ultramega.cabletiers.common.mixin.InvokerTaskImpl;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.Task;
import com.refinedmods.refinedstorage.api.autocrafting.task.TaskId;
import com.refinedmods.refinedstorage.api.network.impl.node.patternprovider.PatternProviderNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNodeActor;
import com.refinedmods.refinedstorage.api.network.node.importer.ImporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.filter.Filter;
import com.refinedmods.refinedstorage.api.resource.filter.FilterMode;
import com.refinedmods.refinedstorage.api.storage.Actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class ExtendedPatternProviderNetworkNode extends PatternProviderNetworkNode {
    private final Filter emptyFilter = new Filter();

    private final Map<Integer, List<ResourceAmount>> patternOutputs = new HashMap<>();
    private final Filter patternOutputFilter = new Filter();

    private final List<ResourceKey> requestedResources = new ArrayList<>();
    private final Filter requestedResourceFilter = new Filter();
    private final Map<TaskId, Set<ResourceKey>> requestedResourcesDeep = new HashMap<>();
    private final Filter requestedResourceFilterDeep = new Filter();

    private final Actor actor = new NetworkNodeActor(this);

    @Nullable
    private Supplier<ImportMode> importMode;
    @Nullable
    private ImporterTransferStrategy transferStrategy;

    public ExtendedPatternProviderNetworkNode(final long energyUsage, final int patterns) {
        super(energyUsage, patterns);

        patternOutputFilter.setMode(FilterMode.ALLOW);
        requestedResourceFilter.setMode(FilterMode.ALLOW);
        requestedResourceFilterDeep.setMode(FilterMode.ALLOW);
    }

    @Override
    public void doWork() {
        super.doWork();
        if (network == null || !isActive() || transferStrategy == null) {
            return;
        }

        final ImportMode mode = importMode != null ? importMode.get() : null;
        if (mode == null || mode == ImportMode.DONT_IMPORT) {
            return;
        }

        final Filter filter = switch (mode) {
            case IMPORT_EVERYTHING -> emptyFilter;
            case IMPORT_PATTERN_OUTPUTS -> patternOutputFilter;
            case IMPORT_REQUESTED_RESOURCES -> requestedResourceFilter;
            case IMPORT_REQUESTED_RESOURCES_DEEP -> requestedResourceFilterDeep;
            default -> null;
        };

        if (filter != null) {
            if (removeCompletedPatternsFromRequestedResourcesDeep()) {
                return;
            }

            transferStrategy.transfer(filter, actor, network);
        }
    }

    @Override
    public void addTask(final Task task) {
        super.addTask(task);

        requestedResources.add(task.getResource());
        requestedResourceFilter.setFilters(new HashSet<>(requestedResources));

        final Set<ResourceKey> resources = collectAllPatternOutputs(task);
        requestedResourcesDeep.put(task.getId(), resources);
        updateDeepFilter();
    }

    public void removeRequestedResourceFilter(final Task task) {
        requestedResources.remove(task.getResource());
        requestedResourceFilter.setFilters(new HashSet<>(requestedResources));

        requestedResourcesDeep.remove(task.getId());
        updateDeepFilter();
    }

    private Set<ResourceKey> collectAllPatternOutputs(final Task task) {
        if (task instanceof InvokerTaskImpl taskImpl) {
            final Set<ResourceKey> resources = new HashSet<>();
            taskImpl.cabletiers$getPatterns().keySet().forEach(pattern ->
                pattern.layout().outputs().forEach(resource ->
                    resources.add(resource.resource())
                )
            );
            return resources;
        }
        return Set.of();
    }

    private void updateDeepFilter() {
        final Set<ResourceKey> mergedResources = requestedResourcesDeep.values().stream()
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
        requestedResourceFilterDeep.setFilters(mergedResources);
    }

    private boolean removeCompletedPatternsFromRequestedResourcesDeep() {
        boolean changed = false;

        for (final Task task : getTasks()) {
            if (!(task instanceof InvokerTaskImpl taskImpl)) {
                continue;
            }

            final TaskId taskId = task.getId();
            final Set<ResourceKey> trackedResources = requestedResourcesDeep.get(taskId);

            if (trackedResources == null || trackedResources.isEmpty()) {
                continue;
            }

            final Set<ResourceKey> toRemove = new HashSet<>();

            for (final InvokerAbstractTaskPattern taskPattern : taskImpl.cabletiers$getCompletedPatterns()) {
                for (final ResourceAmount output : taskPattern.cabletiers$getPattern().layout().outputs()) {
                    final ResourceKey resource = output.resource();
                    if (trackedResources.contains(resource)) {
                        toRemove.add(resource);
                    }
                }
            }

            if (!toRemove.isEmpty()) {
                trackedResources.removeAll(toRemove);
                changed = true;

                if (trackedResources.isEmpty()) {
                    requestedResourcesDeep.remove(taskId);
                }
            }
        }

        if (changed) {
            updateDeepFilter();
        }

        return changed;
    }

    public void setTransferStrategy(final ImporterTransferStrategy transferStrategy) {
        this.transferStrategy = transferStrategy;
    }

    public void updatePatternOutputFilter(final int slot, @Nullable final Pattern pattern) {
        patternOutputs.put(slot, pattern != null ? pattern.layout().outputs() : null);
        final Set<ResourceKey> filters = patternOutputs.values().stream()
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .map(ResourceAmount::resource)
            .collect(Collectors.toSet());
        patternOutputFilter.setFilters(filters);
    }

    public void setImportMode(final Supplier<ImportMode> importMode) {
        this.importMode = importMode;
    }
}
