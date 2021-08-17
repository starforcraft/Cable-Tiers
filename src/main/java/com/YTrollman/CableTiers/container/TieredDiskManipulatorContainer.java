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
        if(getTier() == CableTier.ELITE)
        {
            for (int i = 0; i < 3; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i + 3, 44, 71 + (i * 18)));
            }
            for (int i = 0; i < 3; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i, 26, 71 + (i * 18)));
            }

            for (int i = 0; i < 3; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i + 3, 134, 71 + (i * 18)));
            }
            for (int i = 0; i < 3; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i, 116, 71 + (i * 18)));
            }

            addPlayerInventory(8, 143);
        }
        else if(getTier() == CableTier.ULTRA)
        {
            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i + 4, 44, 87 + (i * 18)));
            }
            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i + 4, 26, 87 + (i * 18)));
            }
            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i, 8, 87 + (i * 18)));
            }

            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i + 4, 152, 87 + (i * 18)));
            }
            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i + 4, 134, 87 + (i * 18)));
            }
            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i, 116, 87 + (i * 18)));
            }

            addPlayerInventory(8, 174);
        }
        else if(getTier() == CableTier.CREATIVE)
        {
            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i + 12, 59, 91 + (i * 18)));
            }
            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i + 8, 41, 91 + (i * 18)));
            }
            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i + 4, 23, 91 + (i * 18)));
            }
            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getInputDisks(), i, 5, 91 + (i * 18)));
            }

            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i + 12, 155, 91 + (i * 18)));
            }
            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i + 8, 137, 91 + (i * 18)));
            }
            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i + 4, 119, 91 + (i * 18)));
            }
            for (int i = 0; i < 4; ++i)
            {
                addSlot(new SlotItemHandler(getNode().getOutputDisks(), i, 101, 91 + (i * 18)));
            }

            addPlayerInventory(8, 173);
        }

        addUpgradeSlots(getNode().getUpgrades());
        addFilterSlots(getNode().getItemFilters(), getNode().getFluidFilters(), getNode());

        transferManager.addBiTransfer(getPlayer().inventory, getNode().getUpgrades());
        transferManager.addBiTransfer(getPlayer().inventory, getNode().getInputDisks());
        transferManager.addTransfer(getNode().getOutputDisks(), getPlayer().inventory);
        transferManager.addFilterTransfer(getPlayer().inventory, getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
