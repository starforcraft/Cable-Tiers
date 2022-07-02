package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.CableTier;
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
        if (getTier() == CableTier.ELITE) {
            for (int i = 0; i < 3; ++i) {
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i + 3, 44, 71 + (i * 18)));
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i, 26, 71 + (i * 18)));

                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i + 3, 134, 71 + (i * 18)));
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i, 116, 71 + (i * 18)));
            }

            addPlayerInventory(8, 143);
        } else if (getTier() == CableTier.ULTRA) {
            for (int i = 0; i < 3; ++i) {
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i + 6, 44, 89 + (i * 18)));
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i + 3, 26, 89 + (i * 18)));
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i, 8, 89 + (i * 18)));

                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i + 6, 152, 89 + (i * 18)));
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i + 3, 134, 89 + (i * 18)));
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i, 116, 89 + (i * 18)));
            }

            addPlayerInventory(8, 161);
        } else if (getTier() == CableTier.CREATIVE) {
            for (int i = 0; i < 3; ++i) {
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i + 9, 59, 106 + (i * 18)));
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i + 6, 41, 106 + (i * 18)));
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i + 3, 23, 106 + (i * 18)));
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i, 5, 106 + (i * 18)));

                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i + 9, 155, 106 + (i * 18)));
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i + 6, 137, 106 + (i * 18)));
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i + 3, 119, 106 + (i * 18)));
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i, 101, 106 + (i * 18)));
            }

            addPlayerInventory(8, 174);
        }

        addUpgradeSlots(getNode().getUpgrades());
        addFilterSlots(getNode().getItemFilters(), getNode().getFluidFilters(), getNode());

        transferManager.addBiTransfer(getPlayer().inventory, getNode().getUpgrades());
        transferManager.addBiTransfer(getPlayer().inventory, getNode().getInputDisks());
        transferManager.addTransfer(getNode().getOutputDisks(), getPlayer().inventory);
        transferManager.addFilterTransfer(getPlayer().inventory, getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
