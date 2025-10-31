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
import javax.annotation.Nullable;

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
        if (network == null || !isActive() || schedulingMode == null) {
            return;
        }
        for (int i = 0; i < getTier().getSpeed(getType()); i++) {
            schedulingMode.execute(tasks);
        }
    }

    public void setTransferStrategy(final ExporterTransferStrategy transferStrategy) {
        this.transferStrategy = transferStrategy;
    }

    public void setSchedulingMode(@Nullable final SchedulingMode schedulingMode) {
        this.schedulingMode = schedulingMode;
    }

    @Nullable
    public ExporterTransferStrategy.Result getLastResult(final int filterIndex, final int fakeIndex) {
        return tasks.get(filterIndex + fakeIndex).lastResult;
    }

    public void setFilters(final List<ResourceKey> filters, final List<ResourceTag> tagFilters) {
        final List<TieredExporterTask> updatedTasks = new ArrayList<>();
        for (int i = 0; i < filters.size(); ++i) {
            for (final ResourceKey resource : getResourcesFromFilter(filters, tagFilters, i)) {
                final ExporterTransferStrategy.Result lastResult = (i < tasks.size() && tasks.get(i).filter.equals(resource))
                    ? tasks.get(i).lastResult
                    : null;
                updatedTasks.add(new TieredExporterTask(resource, lastResult));
            }
        }
        tasks.clear();
        tasks.addAll(updatedTasks);
    }

    class TieredExporterTask implements SchedulingMode.ScheduledTask {
        private final ResourceKey filter;
        @Nullable
        private ExporterTransferStrategy.Result lastResult;

        TieredExporterTask(final ResourceKey filter, @Nullable final ExporterTransferStrategy.Result lastResult) {
            this.filter = filter;
            this.lastResult = lastResult;
        }

        @Override
        public boolean run() {
            if (transferStrategy == null || network == null) {
                return false;
            }
            this.lastResult = transferStrategy.transfer(filter, actor, network);
            return lastResult == ExporterTransferStrategy.Result.EXPORTED;
        }
    }
}
