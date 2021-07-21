package com.YTrollman.CableTiers.container;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredExporterNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredExporterTileEntity;
import com.YTrollman.CableTiers.util.MathUtil;
import com.refinedmods.refinedstorage.container.BaseContainer;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class TieredExporterContainer extends BaseContainer {

    private final CableTier tier;

    private boolean hasRegulatorMode;

    public TieredExporterContainer(int windowId, PlayerEntity player, TieredExporterTileEntity tile, CableTier tier) {
        super(ContentType.EXPORTER.getContainerType(tier), tile, player, windowId);
        this.tier = tier;
        this.hasRegulatorMode = hasRegulatorMode();
        initSlots();
    }

    public CableTier getTier() {
        return tier;
    }

    @Override
    public TieredExporterTileEntity getTile() {
        return (TieredExporterTileEntity) super.getTile();
    }

    private TieredExporterNetworkNode getNode() {
        return getTile().getNode();
    }

    private boolean hasRegulatorMode() {
        return getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        checkRegulator();
    }

    public void checkRegulator() {
        boolean updatedHasRegulatorMode = hasRegulatorMode();
        if (hasRegulatorMode != updatedHasRegulatorMode) {
            hasRegulatorMode = updatedHasRegulatorMode;
            slots.clear();
            lastSlots.clear();
            transferManager.clearTransfers();
            initSlots();
        }
    }

    private void initSlots() {
        for (int i = 0; i < getNode().getUpgrades().getSlots(); i++) {
            addSlot(new SlotItemHandler(getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        boolean hasRegulator = getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR);
        for (int i = 0; i < tier.getSlots(); i++) {
            int x = 8 + (18 * (i % 9));
            int y = 20 + (18 * (i / 9));

            addSlot(new FilterSlot(
                    getNode().getItemFilters(),
                    i, x, y,
                    hasRegulator ? FilterSlot.FILTER_ALLOW_SIZE : 0
            ).setEnableHandler(() -> getNode().getType() == IType.ITEMS));

            addSlot(new FluidFilterSlot(
                    getNode().getFluidFilters(),
                    i, x, y,
                    hasRegulator ? FluidFilterSlot.FILTER_ALLOW_SIZE : 0
            ).setEnableHandler(() -> getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 37 + 18 * MathUtil.ceilDiv(tier.getSlots(), 9));

        transferManager.addBiTransfer(getPlayer().inventory, getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().inventory, getNode().getItemFilters(), getNode().getFluidFilters(), getNode()::getType);
    }
}
