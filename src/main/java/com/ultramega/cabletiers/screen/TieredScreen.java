package com.ultramega.cabletiers.screen;

import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredBlockEntity;
import com.ultramega.cabletiers.container.TieredContainerMenu;
import com.ultramega.cabletiers.node.TieredNetworkNode;
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
