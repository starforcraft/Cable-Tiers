package com.ultramega.cabletiers.common.constructordestructor;

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

import java.util.function.Predicate;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class TieredDestructorContainerMenu extends AbstractTieredFilterContainerMenu<AbstractTieredDestructorBlockEntity> {
    private static final MutableComponent FILTER_HELP = createTranslation("gui", "destructor.filter_help");

    private final Predicate<Player> stillValid;

    public TieredDestructorContainerMenu(final int syncId,
                                         final Inventory playerInventory,
                                         final ResourceContainerData resourceContainerData,
                                         final CableTiers tier) {
        super(Menus.INSTANCE.getTieredDestructors(tier),
            syncId,
            playerInventory.player,
            resourceContainerData,
            AbstractTieredDestructorBlockEntity.getUpgradeDestination(tier),
            tier.getPlayerInventoryY(),
            FILTER_HELP,
            tier);
        this.stillValid = p -> true;
    }

    TieredDestructorContainerMenu(final int syncId,
                                  final Player player,
                                  final AbstractTieredDestructorBlockEntity blockEntity,
                                  final ResourceContainer filterContainer,
                                  final UpgradeContainer upgradeContainer,
                                  final CableTiers tier) {
        super(Menus.INSTANCE.getTieredDestructors(tier),
            syncId,
            player,
            filterContainer,
            upgradeContainer,
            blockEntity,
            tier.getPlayerInventoryY(),
            FILTER_HELP,
            tier);
        this.stillValid = p -> Container.stillValidBlockEntity(blockEntity, p);
    }

    @Override
    protected void registerClientProperties() {
        this.registerProperty(new ClientProperty<>(PropertyTypes.REDSTONE_MODE, RedstoneMode.IGNORE));
        this.registerProperty(new ClientProperty<>(PropertyTypes.FILTER_MODE, FilterMode.BLOCK));
        this.registerProperty(new ClientProperty<>(ConstructorDestructorPropertyTypes.PICKUP_ITEMS, false));
    }

    @Override
    protected void registerServerProperties(final AbstractTieredDestructorBlockEntity blockEntity) {
        this.registerProperty(new ServerProperty<>(
            PropertyTypes.REDSTONE_MODE,
            blockEntity::getRedstoneMode,
            blockEntity::setRedstoneMode
        ));
        this.registerProperty(new ServerProperty<>(
            PropertyTypes.FILTER_MODE,
            blockEntity::getFilterMode,
            blockEntity::setFilterMode
        ));
        this.registerProperty(new ServerProperty<>(
            ConstructorDestructorPropertyTypes.PICKUP_ITEMS,
            blockEntity::isPickupItems,
            blockEntity::setPickupItems
        ));
    }

    @Override
    public boolean stillValid(final Player player) {
        return this.stillValid.test(player);
    }
}
