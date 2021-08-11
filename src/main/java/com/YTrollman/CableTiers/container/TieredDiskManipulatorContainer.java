package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredDiskManipulatorNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredDiskManipulatorTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class TieredDiskManipulatorContainer extends TieredContainer<TieredDiskManipulatorTileEntity, TieredDiskManipulatorNetworkNode> {
    public TieredDiskManipulatorContainer(int windowId, PlayerEntity player, TieredDiskManipulatorTileEntity tile) {
        super(ContentType.DISK_MANIPULATOR, tile, player, windowId);
        initSlots();
    }

    private void initSlots() {
        addUpgradeSlots(getNode().getUpgrades());
        for (int i = 0; i < 3; ++i) {
            addSlot(new SlotItemHandler(getNode().getInputDisks(), i, 44, 57 + (i * 18)));
        }

        for (int i = 0; i < 3; ++i) {
            addSlot(new SlotItemHandler(getNode().getOutputDisks(), i, 116, 57 + (i * 18)));
        }
        addFilterSlots(getNode().getItemFilters(), getNode().getFluidFilters(), getNode());

        addPlayerInventory(8, 129);

        transferManager.addBiTransfer(getPlayer().inventory, getNode().getUpgrades());
        transferManager.addBiTransfer(getPlayer().inventory, getNode().getInputDisks());
        transferManager.addTransfer(getNode().getOutputDisks(), getPlayer().inventory);
        transferManager.addFilterTransfer(getPlayer().inventory, getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
