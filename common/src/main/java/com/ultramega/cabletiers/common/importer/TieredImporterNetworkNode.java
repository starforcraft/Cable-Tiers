package com.ultramega.cabletiers.common.importer;

import com.ultramega.cabletiers.common.advancedfilter.AdvancedFilter;
import com.ultramega.cabletiers.common.utils.TieredSimpleNetworkNode;

import com.refinedmods.refinedstorage.api.network.node.NetworkNodeActor;
import com.refinedmods.refinedstorage.api.network.node.importer.ImporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.filter.FilterMode;
import com.refinedmods.refinedstorage.api.storage.Actor;

import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

import net.minecraft.tags.TagKey;

public class TieredImporterNetworkNode extends TieredSimpleNetworkNode {
    private final AdvancedFilter filter = new AdvancedFilter();
    private final Actor actor = new NetworkNodeActor(this);

    @Nullable
    private ImporterTransferStrategy transferStrategy;

    public TieredImporterNetworkNode(final long energyUsage) {
        super(energyUsage);
    }

    @Override
    public void doWork() {
        super.doWork();
        if (network == null || !isActive() || transferStrategy == null) {
            return;
        }
        for (int i = 0; i < getTier().getSpeed(getType()); i++) {
            transferStrategy.transfer(filter, actor, network);
        }
    }

    public void setTransferStrategy(final ImporterTransferStrategy transferStrategy) {
        this.transferStrategy = transferStrategy;
    }

    public FilterMode getFilterMode() {
        return filter.getMode();
    }

    public void setFilterMode(final FilterMode mode) {
        filter.setMode(mode);
    }

    public void setNormalizer(final UnaryOperator<ResourceKey> normalizer) {
        filter.setNormalizer(normalizer);
    }

    public void setFilters(final Set<ResourceKey> filters, final Set<TagKey<?>> tagFilters) {
        filter.setFilters(filters);
        filter.setTagFilters(tagFilters);
    }
}
