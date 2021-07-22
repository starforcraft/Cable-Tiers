package com.YTrollman.CableTiers.gui;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.container.TieredContainer;
import com.YTrollman.CableTiers.node.TieredNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredTileEntity;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public abstract class TieredScreen<T extends TieredTileEntity<N>, C extends TieredContainer<T, N>, N extends TieredNetworkNode<N>> extends BaseScreen<C> {

    protected TieredScreen(C container, int xSize, int ySize, PlayerInventory inventory, ITextComponent title) {
        super(container, xSize, ySize, inventory, title);
    }

    public ContentType<?, T, C, N> getContentType() {
        return (ContentType<?, T, C, N>) menu.getContentType();
    }

    public CableTier getTier() {
        return menu.getTier();
    }

    public T getTile() {
        return menu.getTile();
    }

    public N getNode() {
        return menu.getNode();
    }

    @Override
    public void tick(int x, int y) {
    }
}
