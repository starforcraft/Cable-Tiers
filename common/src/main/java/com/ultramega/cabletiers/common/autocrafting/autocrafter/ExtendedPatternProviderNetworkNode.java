package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.ultramega.cabletiers.common.mixin.AbstractTaskPatternAccessor;
import com.ultramega.cabletiers.common.mixin.TaskImplInvoker;

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

import org.jspecify.annotations.Nullable;

public class ExtendedPatternProviderNetworkNode extends PatternProviderNetworkNode {
    private final Filter emptyFilter = new Filter();

    private final Map<Integer, @Nullable List<ResourceAmount>> patternOutputs = new HashMap<>();
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

        this.patternOutputFilter.setMode(FilterMode.ALLOW);
        this.requestedResourceFilter.setMode(FilterMode.ALLOW);
        this.requestedResourceFilterDeep.setMode(FilterMode.ALLOW);
    }

    @Override
    public void doWork() {
        super.doWork();
        if (this.network == null || !this.isActive() || this.transferStrategy == null) {
            return;
        }

        final ImportMode mode = this.importMode != null ? this.importMode.get() : null;
        if (mode == null || mode == ImportMode.DONT_IMPORT) {
            return;
        }

        final Filter filter = switch (mode) {
            case IMPORT_EVERYTHING -> this.emptyFilter;
            case IMPORT_PATTERN_OUTPUTS -> this.patternOutputFilter;
            case IMPORT_REQUESTED_RESOURCES -> this.requestedResourceFilter;
            case IMPORT_REQUESTED_RESOURCES_DEEP -> this.requestedResourceFilterDeep;
            default -> null;
        };
        if (filter != null) {
            if (this.removeCompletedPatternsFromRequestedResourcesDeep()) {
                return;
            }

            this.transferStrategy.transfer(filter, this.actor, this.network);
        }
    }

    @Override
    public void addTask(final Task task) {
        super.addTask(task);

        this.requestedResources.add(task.getResource());
        this.requestedResourceFilter.setFilters(new HashSet<>(this.requestedResources));

        final Set<ResourceKey> resources = this.collectAllPatternOutputs(task);
        this.requestedResourcesDeep.put(task.getId(), resources);
        this.updateDeepFilter();
    }

    public void removeRequestedResourceFilter(final Task task) {
        this.requestedResources.remove(task.getResource());
        this.requestedResourceFilter.setFilters(new HashSet<>(this.requestedResources));

        this.requestedResourcesDeep.remove(task.getId());
        this.updateDeepFilter();
    }

    private Set<ResourceKey> collectAllPatternOutputs(final Task task) {
        if (task instanceof TaskImplInvoker taskImpl) {
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
        final Set<ResourceKey> mergedResources = this.requestedResourcesDeep.values().stream()
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
        this.requestedResourceFilterDeep.setFilters(mergedResources);
    }

    private boolean removeCompletedPatternsFromRequestedResourcesDeep() {
        boolean changed = false;

        for (final Task task : this.getTasks()) {
            if (!(task instanceof TaskImplInvoker taskImpl)) {
                continue;
            }

            final TaskId taskId = task.getId();
            final Set<ResourceKey> trackedResources = this.requestedResourcesDeep.get(taskId);

            if (trackedResources == null || trackedResources.isEmpty()) {
                continue;
            }

            final Set<ResourceKey> toRemove = new HashSet<>();

            for (final AbstractTaskPatternAccessor taskPattern : taskImpl.cabletiers$getCompletedPatterns()) {
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
                    this.requestedResourcesDeep.remove(taskId);
                }
            }
        }

        if (changed) {
            this.updateDeepFilter();
        }

        return changed;
    }

    public void setTransferStrategy(final ImporterTransferStrategy transferStrategy) {
        this.transferStrategy = transferStrategy;
    }

    public void updatePatternOutputFilter(final int slot, @Nullable final Pattern pattern) {
        this.patternOutputs.put(slot, pattern != null ? pattern.layout().outputs() : null);
        final Set<ResourceKey> filters = this.patternOutputs.values().stream()
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .map(ResourceAmount::resource)
            .collect(Collectors.toSet());
        this.patternOutputFilter.setFilters(filters);
    }

    public void setImportMode(final Supplier<ImportMode> importMode) {
        this.importMode = importMode;
    }
}
