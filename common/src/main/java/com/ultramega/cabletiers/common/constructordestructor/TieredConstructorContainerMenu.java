package com.ultramega.cabletiers.common.constructordestructor;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.exporter.TieredExportingIndicators;
import com.ultramega.cabletiers.common.registry.Menus;
import com.ultramega.cabletiers.common.support.AbstractTieredFilterContainerMenu;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.constructordestructor.ConstructorData;
import com.refinedmods.refinedstorage.common.support.RedstoneMode;
import com.refinedmods.refinedstorage.common.support.SchedulingModeType;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicatorListener;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;

import java.util.function.Predicate;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class TieredConstructorContainerMenu extends AbstractTieredFilterContainerMenu<AbstractTieredConstructorBlockEntity>
    implements ExportingIndicatorListener {
    private static final MutableComponent FILTER_HELP = createTranslation("gui", "constructor.filter_help");

    private final TieredExportingIndicators indicators;
    private final Predicate<Player> stillValid;

    public TieredConstructorContainerMenu(final int syncId,
                                          final Inventory playerInventory,
                                          final ConstructorData data,
                                          final CableTiers tier) {
        super(Menus.INSTANCE.getTieredConstructors(tier),
            syncId,
            playerInventory.player,
            data.resourceContainerData(),
            AbstractTieredConstructorBlockEntity.getUpgradeDestination(tier),
            tier.getPlayerInventoryY(),
            FILTER_HELP,
            tier);
        this.indicators = new TieredExportingIndicators(data.exportingIndicators());
        this.stillValid = p -> true;
    }

    TieredConstructorContainerMenu(final int syncId,
                                   final Player player,
                                   final AbstractTieredConstructorBlockEntity blockEntity,
                                   final ResourceContainer filterContainer,
                                   final UpgradeContainer upgradeContainer,
                                   final TieredExportingIndicators indicators,
                                   final CableTiers tier) {
        super(Menus.INSTANCE.getTieredConstructors(tier),
            syncId,
            player,
            filterContainer,
            upgradeContainer,
            blockEntity,
            tier.getPlayerInventoryY(),
            FILTER_HELP,
            tier);
        this.indicators = indicators;
        this.stillValid = p -> Container.stillValidBlockEntity(blockEntity, p);
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
        registerProperty(new ClientProperty<>(ConstructorDestructorPropertyTypes.DROP_ITEMS, false));
    }

    @Override
    protected void registerServerProperties(final AbstractTieredConstructorBlockEntity blockEntity) {
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
        registerProperty(new ServerProperty<>(
            ConstructorDestructorPropertyTypes.DROP_ITEMS,
            blockEntity::isDropItems,
            blockEntity::setDropItems
        ));
    }

    @Override
    public void indicatorChanged(final int index, final ExportingIndicator indicator) {
        indicators.set(index, indicator);
    }

    @Override
    public boolean stillValid(final Player player) {
        return stillValid.test(player);
    }
}
