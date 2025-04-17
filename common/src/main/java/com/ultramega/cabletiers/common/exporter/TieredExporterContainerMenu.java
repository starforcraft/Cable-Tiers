package com.ultramega.cabletiers.common.exporter;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.registry.Menus;
import com.ultramega.cabletiers.common.support.AbstractTieredFilterContainerMenu;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.exporter.ExporterData;
import com.refinedmods.refinedstorage.common.support.RedstoneMode;
import com.refinedmods.refinedstorage.common.support.SchedulingModeType;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicatorListener;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class TieredExporterContainerMenu extends AbstractTieredFilterContainerMenu<AbstractTieredExporterBlockEntity> implements ExportingIndicatorListener {
    private static final MutableComponent FILTER_HELP = createTranslation("gui", "exporter.filter_help");

    private final TieredExportingIndicators indicators;

    TieredExporterContainerMenu(final int syncId,
                                final Player player,
                                final AbstractTieredExporterBlockEntity blockEntity,
                                final ResourceContainer filterContainer,
                                final UpgradeContainer upgradeContainer,
                                final TieredExportingIndicators indicators,
                                final CableTiers tier) {
        super(Menus.INSTANCE.getTieredExporters(tier),
            syncId,
            player,
            filterContainer,
            upgradeContainer,
            blockEntity,
            tier.getPlayerInventoryY(),
            FILTER_HELP,
            tier);
        this.indicators = indicators;
    }

    public TieredExporterContainerMenu(final int syncId,
                                       final Inventory playerInventory,
                                       final ExporterData data,
                                       final CableTiers tier) {
        super(Menus.INSTANCE.getTieredExporters(tier),
            syncId,
            playerInventory.player,
            data.resourceContainerData(),
            AbstractTieredExporterBlockEntity.getUpgradeDestination(tier),
            tier.getPlayerInventoryY(),
            FILTER_HELP,
            tier);
        this.indicators = new TieredExportingIndicators(data.exportingIndicators());
    }

    ExportingIndicator getIndicator(final int idx) {
        return indicators.get(idx);
    }

    int getIndicators() {
        return indicators.size();
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (player instanceof ServerPlayer serverPlayer) {
            indicators.detectChanges(serverPlayer);
        }
    }

    @Override
    protected void registerClientProperties() {
        registerProperty(new ClientProperty<>(PropertyTypes.FUZZY_MODE, false));
        registerProperty(new ClientProperty<>(PropertyTypes.REDSTONE_MODE, RedstoneMode.IGNORE));
        registerProperty(new ClientProperty<>(PropertyTypes.SCHEDULING_MODE, SchedulingModeType.DEFAULT));
    }

    @Override
    protected void registerServerProperties(final AbstractTieredExporterBlockEntity blockEntity) {
        registerProperty(new ServerProperty<>(
            PropertyTypes.FUZZY_MODE,
            blockEntity::isFuzzyMode,
            blockEntity::setFuzzyMode
        ));
        registerProperty(new ServerProperty<>(
            PropertyTypes.REDSTONE_MODE,
            blockEntity::getRedstoneMode,
            blockEntity::setRedstoneMode
        ));
        registerProperty(new ServerProperty<>(
            PropertyTypes.SCHEDULING_MODE,
            blockEntity::getSchedulingModeType,
            blockEntity::setSchedulingModeType
        ));
    }

    @Override
    public void indicatorChanged(final int index, final ExportingIndicator indicator) {
        indicators.set(index, indicator);
    }
}
