package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.refinedmods.refinedstorage.api.network.impl.node.patternprovider.PatternProviderNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNodeActor;
import com.refinedmods.refinedstorage.api.network.node.importer.ImporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.filter.Filter;
import com.refinedmods.refinedstorage.api.resource.filter.FilterMode;
import com.refinedmods.refinedstorage.api.storage.Actor;

import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class ExtendedPatternProviderNetworkNode extends PatternProviderNetworkNode {
    private final Filter filter = new Filter();
    private final Actor actor = new NetworkNodeActor(this);

    @Nullable
    private Supplier<Boolean> actAsImporter;
    @Nullable
    private ImporterTransferStrategy transferStrategy;

    public ExtendedPatternProviderNetworkNode(final long energyUsage, final int patterns) {
        super(energyUsage, patterns);
    }

    @Override
    public void doWork() {
        super.doWork();
        if (network == null || !isActive() || transferStrategy == null) {
            return;
        }
        if (actAsImporter != null && !actAsImporter.get()) {
            return;
        }
        transferStrategy.transfer(filter, actor, network);
    }

    public void setTransferStrategy(final ImporterTransferStrategy transferStrategy) {
        this.transferStrategy = transferStrategy;
    }

    public void setFilters(final Set<ResourceKey> filters) {
        filter.setMode(FilterMode.ALLOW);
        filter.setFilters(filters);
    }

    public void setActAsImporter(final Supplier<Boolean> actAsImporter) {
        this.actAsImporter = actAsImporter;
    }
}
