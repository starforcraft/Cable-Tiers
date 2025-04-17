package com.ultramega.cabletiers.common.importer;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.registry.Menus;
import com.ultramega.cabletiers.common.support.AbstractTieredFilterContainerMenu;

import com.refinedmods.refinedstorage.api.resource.filter.FilterMode;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.support.RedstoneMode;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class TieredImporterContainerMenu extends AbstractTieredFilterContainerMenu<AbstractTieredImporterBlockEntity> {
    private static final MutableComponent FILTER_HELP = createTranslation("gui", "importer.filter_help");

    TieredImporterContainerMenu(final int syncId,
                                final Player player,
                                final AbstractTieredImporterBlockEntity blockEntity,
                                final ResourceContainer filterContainer,
                                final UpgradeContainer upgradeContainer,
                                final CableTiers tier) {
        super(Menus.INSTANCE.getTieredImporters(tier),
            syncId,
            player,
            filterContainer,
            upgradeContainer,
            blockEntity,
            tier.getPlayerInventoryY(),
            FILTER_HELP,
            tier);
    }

    public TieredImporterContainerMenu(final int syncId,
                                       final Inventory playerInventory,
                                       final ResourceContainerData resourceContainerData,
                                       final CableTiers tier) {
        super(Menus.INSTANCE.getTieredImporters(tier),
            syncId,
            playerInventory.player,
            resourceContainerData,
            AbstractTieredImporterBlockEntity.getUpgradeDestination(tier),
            tier.getPlayerInventoryY(),
            FILTER_HELP,
            tier);
    }

    @Override
    protected void registerClientProperties() {
        registerProperty(new ClientProperty<>(PropertyTypes.FILTER_MODE, FilterMode.BLOCK));
        registerProperty(new ClientProperty<>(PropertyTypes.FUZZY_MODE, false));
        registerProperty(new ClientProperty<>(PropertyTypes.REDSTONE_MODE, RedstoneMode.IGNORE));
    }

    @Override
    protected void registerServerProperties(final AbstractTieredImporterBlockEntity blockEntity) {
        registerProperty(new ServerProperty<>(
            PropertyTypes.FILTER_MODE,
            blockEntity::getFilterMode,
            blockEntity::setFilterMode
        ));
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
    }
}
