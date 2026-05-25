package com.ultramega.cabletiers.common.constructordestructor;

import com.ultramega.cabletiers.common.advancedfilter.AdvancedFilter;
import com.ultramega.cabletiers.common.utils.TieredSimpleNetworkNode;

import com.refinedmods.refinedstorage.api.network.node.NetworkNodeActor;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.filter.FilterMode;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.api.constructordestructor.DestructorStrategy;

import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public class TieredDestructorNetworkNode extends TieredSimpleNetworkNode {
    private final AdvancedFilter filter = new AdvancedFilter();
    private final Actor actor = new NetworkNodeActor(this);

    @Nullable
    private DestructorStrategy strategy;
    @Nullable
    private Supplier<Player> playerProvider;

    TieredDestructorNetworkNode(final long energyUsage) {
        super(energyUsage);
    }

    void setStrategy(@Nullable final DestructorStrategy strategy) {
        this.strategy = strategy;
    }

    void setPlayerProvider(@Nullable final Supplier<Player> playerProvider) {
        this.playerProvider = playerProvider;
    }

    FilterMode getFilterMode() {
        return this.filter.getMode();
    }

    void setFilterMode(final FilterMode mode) {
        this.filter.setMode(mode);
    }

    public void setFilters(final Set<ResourceKey> filters, final Set<TagKey<?>> tagFilters) {
        this.filter.setFilters(filters);
        this.filter.setTagFilters(tagFilters);
    }

    @Override
    public void doWork() {
        super.doWork();
        if (this.strategy == null || this.network == null || !this.isActive() || this.playerProvider == null) {
            return;
        }
        final Player player = this.playerProvider.get();
        for (int i = 0; i < this.getTier().getSpeed(this.getType()); i++) {
            this.strategy.apply(this.filter, this.actor, this::getNetwork, player);
        }
    }
}
