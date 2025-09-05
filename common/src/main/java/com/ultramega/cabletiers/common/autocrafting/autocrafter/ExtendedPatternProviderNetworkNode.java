package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.Task;
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

    private final Actor actor = new NetworkNodeActor(this);

    @Nullable
    private Supplier<ImportMode> importMode;
    @Nullable
    private ImporterTransferStrategy transferStrategy;

    public ExtendedPatternProviderNetworkNode(final long energyUsage, final int patterns) {
        super(energyUsage, patterns);

        patternOutputFilter.setMode(FilterMode.ALLOW);
        requestedResourceFilter.setMode(FilterMode.ALLOW);
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
            default -> null;
        };

        if (filter != null) {
            transferStrategy.transfer(filter, actor, network);
        }
    }

    @Override
    public void addTask(final Task task) {
        super.addTask(task);

        requestedResources.add(task.getResource());
        requestedResourceFilter.setFilters(new HashSet<>(requestedResources));
    }

    public void removeRequestedResourceFilter(final Task task) {
        requestedResources.remove(task.getResource());
        requestedResourceFilter.setFilters(new HashSet<>(requestedResources));
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
