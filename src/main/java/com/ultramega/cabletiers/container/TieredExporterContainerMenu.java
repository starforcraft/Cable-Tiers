package com.ultramega.cabletiers.container;

import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredExporterBlockEntity;
import com.ultramega.cabletiers.node.TieredExporterNetworkNode;
import com.ultramega.cabletiers.util.MathUtil;
import net.minecraft.world.entity.player.Player;

public class TieredExporterContainerMenu extends TieredContainerMenu<TieredExporterBlockEntity, TieredExporterNetworkNode> {
    private boolean hasRegulatorMode;

    public TieredExporterContainerMenu(int windowId, Player player, TieredExporterBlockEntity tile) {
        super(ContentType.EXPORTER, tile, player, windowId);
        this.hasRegulatorMode = hasRegulatorMode();

        initSlots();
    }

    private boolean hasRegulatorMode() {
        return getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        boolean updatedHasRegulatorMode = hasRegulatorMode();
        if (hasRegulatorMode != updatedHasRegulatorMode) {
            hasRegulatorMode = updatedHasRegulatorMode;

            initSlots();
        }
    }

    public void initSlots() {
        this.slots.clear();
        this.lastSlots.clear();

        this.transferManager.clearTransfers();

        addUpgradeSlots(getNode().getUpgrades());
        addFilterSlots(getNode().getItemFilters(), hasRegulatorMode ? FilterSlot.FILTER_ALLOW_SIZE : 0, getNode().getFluidFilters(), hasRegulatorMode ? FluidFilterSlot.FILTER_ALLOW_SIZE : 0, getNode());
        addPlayerInventory(8, 37 + 18 * MathUtil.ceilDiv(9 * getTier().getSlotsMultiplier(), 9));

        transferManager.addBiTransfer(getPlayer().getInventory(), getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().getInventory(), getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
