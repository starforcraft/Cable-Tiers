package com.ultramega.cabletiers.container;

import com.refinedmods.refinedstorage.container.slot.OutputSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredInterfaceBlockEntity;
import com.ultramega.cabletiers.container.slot.TieredFilterSlot;
import com.ultramega.cabletiers.node.TieredInterfaceNetworkNode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class TieredInterfaceContainerMenu extends TieredContainerMenu<TieredInterfaceBlockEntity, TieredInterfaceNetworkNode> {
    public TieredInterfaceContainerMenu(int windowId, Player player, TieredInterfaceBlockEntity tile) {
        super(ContentType.INTERFACE, tile, player, windowId);
        initSlots();
    }

    private void initSlots() {
        int additionalSlots = switch (getTier()) {
            case ELITE -> 0;
            case ULTRA -> 2;
            case MEGA -> 4;
        };

        for (int i = 0; i < 9 + additionalSlots; ++i) {
            addSlot(new SlotItemHandler(getNode().getImportItems(), i, 8 + (18 * i), 20));
            addSlot(new SlotItemHandler(getNode().getImportItems(), i + 9 + additionalSlots, 8 + (18 * i), 38));
        }

        for (int i = 0; i < 9 + additionalSlots; ++i) {
            addSlot(new TieredFilterSlot(getTier(), getNode().getExportFilterItems(), i, 8 + (18 * i), 72, FilterSlot.FILTER_ALLOW_SIZE));
            addSlot(new TieredFilterSlot(getTier(), getNode().getExportFilterItems(), i + 9 + additionalSlots, 8 + (18 * i), 90, FilterSlot.FILTER_ALLOW_SIZE));
        }

        for (int i = 0; i < 9 + additionalSlots; ++i) {
            addSlot(new OutputSlot(getNode().getExportItems(), i, 8 + (18 * i), 132));
            addSlot(new OutputSlot(getNode().getExportItems(), i + 9 + additionalSlots, 8 + (18 * i), 150));
        }

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(getNode().getUpgrades(), i, 187 + (18 * additionalSlots), 6 + (i * 18)));
        }

        addPlayerInventory(getTier() == CableTier.ELITE ? 8 : getTier() == CableTier.ULTRA ? 26 : 35, 184);

        transferManager.addBiTransfer(getPlayer().getInventory(), getNode().getUpgrades());
        transferManager.addBiTransfer(getPlayer().getInventory(), getNode().getImportItems());
        transferManager.addTransfer(getNode().getExportItems(), getPlayer().getInventory());
    }
}
