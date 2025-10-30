package com.ultramega.cabletiers.common.iface;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.registry.Menus;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.iface.InterfaceData;
import com.refinedmods.refinedstorage.common.support.FilterWithFuzzyMode;
import com.refinedmods.refinedstorage.common.support.RedstoneMode;
import com.refinedmods.refinedstorage.common.support.containermenu.AbstractResourceContainerMenu;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlotType;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicatorListener;
import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicators;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class TieredInterfaceContainerMenu extends AbstractResourceContainerMenu implements ExportingIndicatorListener {
    private static final int EXPORT_CONFIG_SLOT_X = 8;
    private static final int EXPORT_CONFIG_SLOT_Y = 20;
    private static final int EXPORT_OUTPUT_SLOT_Y = 66 + 18;

    private final ExportingIndicators indicators;
    private final Predicate<Player> stillValid;
    private final List<Slot> exportedResourceSlots = new ArrayList<>();

    private final CableTiers tier;

    TieredInterfaceContainerMenu(final int syncId,
                                 final Player player,
                                 final TieredInterfaceBlockEntity blockEntity,
                                 final ResourceContainer exportConfig,
                                 final ResourceContainer exportedResources,
                                 final Container exportedResourcesAsContainer,
                                 final UpgradeContainer upgradeContainer,
                                 final ExportingIndicators indicators,
                                 final CableTiers tier) {
        super(Menus.INSTANCE.getTieredInterfaces(tier), syncId, player);
        this.tier = tier;
        addSlots(player, exportConfig, exportedResources, exportedResourcesAsContainer, upgradeContainer);
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
        this.indicators = indicators;
        this.stillValid = p -> Container.stillValidBlockEntity(blockEntity, p);
    }

    public TieredInterfaceContainerMenu(final int syncId,
                                        final Inventory playerInventory,
                                        final InterfaceData interfaceData,
                                        final CableTiers tier) {
        super(Menus.INSTANCE.getTieredInterfaces(tier), syncId);
        this.tier = tier;
        final ResourceContainer filterContainer = TieredInterfaceBlockEntity.createFilterContainer(tier, interfaceData);
        final ResourceContainer exportedResources = TieredInterfaceBlockEntity.createExportedResourcesContainer(
            tier,
            interfaceData,
            FilterWithFuzzyMode.create(filterContainer, null)
        );
        addSlots(playerInventory.player, filterContainer, exportedResources, exportedResources.toItemContainer(),
            new UpgradeContainer(UpgradeDestinations.INTERFACE, 1));
        registerProperty(new ClientProperty<>(PropertyTypes.FUZZY_MODE, false));
        registerProperty(new ClientProperty<>(PropertyTypes.REDSTONE_MODE, RedstoneMode.IGNORE));
        this.indicators = new ExportingIndicators(interfaceData.exportingIndicators());
        this.stillValid = p -> true;
    }

    private void addSlots(final Player player,
                          final ResourceContainer exportConfig,
                          final ResourceContainer exportedResources,
                          final Container exportedResourcesAsContainer,
                          final UpgradeContainer upgradeContainer) {
        for (int i = 0; i < exportConfig.size(); ++i) {
            addSlot(createExportConfigSlot(exportConfig, i));
        }
        for (int i = 0; i < exportedResources.size(); ++i) {
            exportedResourceSlots.add(addSlot(addExportedResourceSlot(exportedResources, exportedResourcesAsContainer, i)));
        }
        addSlot(new UpgradeSlot(upgradeContainer, 0, 187, 6));
        addPlayerInventory(player.getInventory(), 8, 136 + (tier != CableTiers.ELITE ? 18 : 0));
        transferManager.addBiTransfer(exportedResourcesAsContainer, player.getInventory());
        transferManager.addFilterTransfer(player.getInventory());
    }

    private Slot createExportConfigSlot(final ResourceContainer exportConfig, final int index) {
        return new ResourceSlot(
            exportConfig,
            index,
            createTranslation("gui", "interface.filter_help"),
            getExportSlotX(index),
            EXPORT_CONFIG_SLOT_Y + getExportSlotY(index),
            ResourceSlotType.FILTER_WITH_AMOUNT
        );
    }

    private Slot addExportedResourceSlot(final ResourceContainer exportedResources,
                                         final Container exportedResourcesAsContainer,
                                         final int index) {
        return new ResourceSlot(
            exportedResources,
            exportedResourcesAsContainer,
            index,
            Component.empty(),
            getExportSlotX(index),
            EXPORT_OUTPUT_SLOT_Y + getExportSlotY(index),
            ResourceSlotType.CONTAINER
        ) {
            @Override
            public boolean shouldRenderAmount() {
                return true;
            }
        };
    }

    public List<Slot> getExportedResourceSlots() {
        return exportedResourceSlots;
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
    public boolean stillValid(final Player player) {
        return stillValid.test(player);
    }

    @Override
    public void indicatorChanged(final int index, final ExportingIndicator indicator) {
        indicators.set(index, indicator);
    }

    private static int getExportSlotX(final int index) {
        return EXPORT_CONFIG_SLOT_X + (18 * (index % 9));
    }

    private static int getExportSlotY(final int index) {
        return (18 * (index / 9));
    }
}
