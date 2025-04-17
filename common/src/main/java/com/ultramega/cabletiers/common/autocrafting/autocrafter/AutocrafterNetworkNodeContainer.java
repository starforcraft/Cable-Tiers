package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.refinedmods.refinedstorage.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage.common.api.autocrafting.Autocrafter;
import com.refinedmods.refinedstorage.common.api.support.network.ConnectionStrategy;
import com.refinedmods.refinedstorage.common.support.network.InWorldNetworkNodeContainerImpl;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;

/**
 * Nearly exact copy of {@link com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterNetworkNodeContainer}
 */
class AutocrafterNetworkNodeContainer extends InWorldNetworkNodeContainerImpl implements Autocrafter {
    private final TieredAutocrafterBlockEntity blockEntity;

    AutocrafterNetworkNodeContainer(final TieredAutocrafterBlockEntity blockEntity,
                                    final NetworkNode node,
                                    final String name,
                                    final ConnectionStrategy connectionStrategy) {
        super(blockEntity, node, name, 0, connectionStrategy, null);
        this.blockEntity = blockEntity;
    }

    @Override
    public Component getAutocrafterName() {
        return blockEntity.getName();
    }

    @Override
    public Container getPatternContainer() {
        return blockEntity.getPatternContainer();
    }

    @Override
    public boolean isVisibleToTheAutocrafterManager() {
        return blockEntity.isVisibleToTheAutocrafterManager();
    }
}
