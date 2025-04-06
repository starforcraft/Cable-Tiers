package com.ultramega.cabletiers.common.constructordestructor;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNodeActor;
import com.refinedmods.refinedstorage.api.network.node.SchedulingMode;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.api.constructordestructor.ConstructorStrategy;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;

import static com.ultramega.cabletiers.common.advancedfilter.TagFilterWithFuzzyMode.getResourcesFromFilter;

public class TieredConstructorNetworkNode extends SimpleNetworkNode {
    private final Actor actor = new NetworkNodeActor(this);
    private final List<TieredConstructorTask> tasks = new ArrayList<>();

    @Nullable
    private Supplier<Player> playerProvider;
    @Nullable
    private ConstructorStrategy strategy;
    @Nullable
    private SchedulingMode schedulingMode;

    public TieredConstructorNetworkNode(final long energyUsage) {
        super(energyUsage);
    }

    @Override
    public void doWork() {
        super.doWork();
        if (network == null || !isActive() || schedulingMode == null) {
            return;
        }
        schedulingMode.execute(tasks);
    }

    void setStrategy(@Nullable final ConstructorStrategy strategy) {
        this.strategy = strategy;
    }

    void setPlayerProvider(@Nullable final Supplier<Player> playerSupplier) {
        this.playerProvider = playerSupplier;
    }

    void setSchedulingMode(@Nullable final SchedulingMode schedulingMode) {
        this.schedulingMode = schedulingMode;
    }

    @Nullable
    public ConstructorStrategy.Result getLastResult(final int filterIndex, final int fakeIndex) {
        return tasks.get(filterIndex + fakeIndex).lastResult;
    }

    void setFilters(final List<ResourceKey> filters, final List<ResourceTag> tagFilters) {
        final List<TieredConstructorTask> updatedTasks = new ArrayList<>();
        for (int i = 0; i < filters.size(); ++i) {
            for (final ResourceKey resource : getResourcesFromFilter(filters, tagFilters, i)) {
                final ConstructorStrategy.Result lastResult = (i < tasks.size() && tasks.get(i).filter.equals(resource))
                    ? tasks.get(i).lastResult
                    : null;
                updatedTasks.add(new TieredConstructorTask(resource, lastResult));
            }
        }
        tasks.clear();
        tasks.addAll(updatedTasks);
    }

    class TieredConstructorTask implements SchedulingMode.ScheduledTask {
        private final ResourceKey filter;
        @Nullable
        private ConstructorStrategy.Result lastResult;

        private TieredConstructorTask(final ResourceKey filter, @Nullable final ConstructorStrategy.Result lastResult) {
            this.filter = filter;
            this.lastResult = lastResult;
        }

        @Override
        public boolean run() {
            if (strategy == null || network == null || playerProvider == null) {
                return false;
            }
            final Player player = playerProvider.get();
            this.lastResult = strategy.apply(filter, actor, player, network);
            return lastResult == ConstructorStrategy.Result.SUCCESS;
        }
    }
}
