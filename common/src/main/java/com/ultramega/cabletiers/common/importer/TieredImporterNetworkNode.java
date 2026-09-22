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

import net.minecraft.tags.TagKey;
import org.jspecify.annotations.Nullable;

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
        if (this.network == null || !this.isActive() || this.transferStrategy == null) {
            return;
        }
        final int speed = this.getTier().getSpeed(this.getType());
        for (int i = 0; i < speed; i++) {
            if (!this.transferStrategy.transfer(this.filter, this.actor, this.network)) {
                break;
            }
        }
    }

    public void setTransferStrategy(final ImporterTransferStrategy transferStrategy) {
        this.transferStrategy = transferStrategy;
    }

    public FilterMode getFilterMode() {
        return this.filter.getMode();
    }

    public void setFilterMode(final FilterMode mode) {
        this.filter.setMode(mode);
    }

    public void setNormalizer(final UnaryOperator<ResourceKey> normalizer) {
        this.filter.setNormalizer(normalizer);
    }

    public void setFilters(final Set<ResourceKey> filters, final Set<TagKey<?>> tagFilters) {
        this.filter.setFilters(filters);
        this.filter.setTagFilters(tagFilters);
    }
}
