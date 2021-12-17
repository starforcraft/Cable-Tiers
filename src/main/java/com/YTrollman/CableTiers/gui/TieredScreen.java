package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.blockentity.TieredBlockEntity;
import com.YTrollman.CableTiers.container.TieredContainerMenu;
import com.YTrollman.CableTiers.node.TieredNetworkNode;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class TieredScreen<T extends TieredBlockEntity<N>, C extends TieredContainerMenu<T, N>, N extends TieredNetworkNode<N>> extends BaseScreen<C> {

    protected TieredScreen(C container, int xSize, int ySize, Inventory inventory, Component title) {
        super(container, xSize, ySize, inventory, title);
    }

    public ContentType<?, T, C, N> getContentType() {
        return (ContentType<?, T, C, N>) menu.getContentType();
    }

    public CableTier getTier() {
        return menu.getTier();
    }

    public T getBlockEntity() {
        return menu.getBlockEntity();
    }

    public N getNode() {
        return menu.getNode();
    }

    @Override
    public void tick(int x, int y) {
    }
}
