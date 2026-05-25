package com.ultramega.cabletiers.common.exporter;

import com.ultramega.cabletiers.common.utils.TieredSimpleNetworkNode;

import com.refinedmods.refinedstorage.api.network.node.NetworkNodeActor;
import com.refinedmods.refinedstorage.api.network.node.SchedulingMode;
import com.refinedmods.refinedstorage.api.network.node.exporter.ExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import static com.ultramega.cabletiers.common.advancedfilter.TagFilterWithFuzzyMode.getResourcesFromFilter;

public class TieredExporterNetworkNode extends TieredSimpleNetworkNode {
    private final Actor actor = new NetworkNodeActor(this);
    private final List<TieredExporterTask> tasks = new ArrayList<>();

    @Nullable
    private ExporterTransferStrategy transferStrategy;
    @Nullable
    private SchedulingMode schedulingMode;

    public TieredExporterNetworkNode(final long energyUsage) {
        super(energyUsage);
    }

    @Override
    public void doWork() {
        super.doWork();
        if (this.network == null || !this.isActive() || this.schedulingMode == null) {
            return;
        }
        for (int i = 0; i < this.getTier().getSpeed(this.getType()); i++) {
            this.schedulingMode.execute(this.tasks);
        }
    }

    public void setTransferStrategy(final ExporterTransferStrategy transferStrategy) {
        this.transferStrategy = transferStrategy;
    }

    public void setSchedulingMode(@Nullable final SchedulingMode schedulingMode) {
        this.schedulingMode = schedulingMode;
    }

    public ExporterTransferStrategy.@Nullable Result getLastResult(final int filterIndex, final int fakeIndex) {
        return this.tasks.get(filterIndex + fakeIndex).lastResult;
    }

    public void setFilters(final List<ResourceKey> filters, final List<ResourceTag> tagFilters) {
        final List<TieredExporterTask> updatedTasks = new ArrayList<>();
        for (int i = 0; i < filters.size(); ++i) {
            for (final ResourceKey resource : getResourcesFromFilter(filters, tagFilters, i)) {
                final ExporterTransferStrategy.Result lastResult = (i < this.tasks.size() && this.tasks.get(i).filter.equals(resource))
                    ? this.tasks.get(i).lastResult
                    : null;
                updatedTasks.add(new TieredExporterTask(resource, lastResult));
            }
        }
        this.tasks.clear();
        this.tasks.addAll(updatedTasks);
    }

    class TieredExporterTask implements SchedulingMode.ScheduledTask {
        private final ResourceKey filter;
        private ExporterTransferStrategy.@Nullable Result lastResult;

        TieredExporterTask(final ResourceKey filter, final ExporterTransferStrategy.@Nullable Result lastResult) {
            this.filter = filter;
            this.lastResult = lastResult;
        }

        @Override
        public boolean run() {
            if (transferStrategy == null || network == null) {
                return false;
            }
            this.lastResult = transferStrategy.transfer(this.filter,
                actor, network);
            return this.lastResult == ExporterTransferStrategy.Result.EXPORTED;
        }
    }
}
