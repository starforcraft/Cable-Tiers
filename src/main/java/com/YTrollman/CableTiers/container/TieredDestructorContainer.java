package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredDestructorNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredDestructorTileEntity;
import com.YTrollman.CableTiers.util.MathUtil;
import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class TieredDestructorContainer extends BaseContainer {

    private final CableTier tier;

    public TieredDestructorContainer(int windowId, PlayerEntity player, TieredDestructorTileEntity tile, CableTier tier) {
        super(ContentType.DESTRUCTOR.getContainerType(tier), tile, player, windowId);
        this.tier = tier;
        initSlots();
    }

    public CableTier getTier() {
        return tier;
    }

    @Override
    public TieredDestructorTileEntity getTile() {
        return (TieredDestructorTileEntity) super.getTile();
    }

    private TieredDestructorNetworkNode getNode() {
        return getTile().getNode();
    }

    private void initSlots() {
        for (int i = 0; i < getNode().getUpgrades().getSlots(); i++) {
            addSlot(new SlotItemHandler(getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < tier.getSlots(); i++) {
            int x = 8 + (18 * (i % 9));
            int y = 20 + (18 * (i / 9));

            addSlot(new FilterSlot(
                    getNode().getItemFilters(),
                    i, x, y
            ).setEnableHandler(() -> getNode().getType() == IType.ITEMS));

            addSlot(new FluidFilterSlot(
                    getNode().getFluidFilters(),
                    i, x, y
            ).setEnableHandler(() -> getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 37 + 18 * MathUtil.ceilDiv(tier.getSlots(), 9));

        transferManager.addBiTransfer(getPlayer().inventory, getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().inventory, getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
