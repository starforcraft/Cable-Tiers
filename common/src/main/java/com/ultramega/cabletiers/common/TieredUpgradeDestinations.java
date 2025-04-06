package com.ultramega.cabletiers.common;

import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeDestination;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public enum TieredUpgradeDestinations implements UpgradeDestination {
    IMPORTER_NO_STACK(),
    IMPORTER_NO_STACK_SPEED(),
    EXPORTER_NO_STACK(),
    EXPORTER_NO_STACK_SPEED(),
    DESTRUCTOR_NO_SPEED(),
    CONSTRUCTOR_NO_STACK(),
    CONSTRUCTOR_NO_STACK_SPEED(),
    DISK_INTERFACE_NO_STACK();

    TieredUpgradeDestinations() {
    }

    @Override
    public Component getName() {
        return Component.empty();
    }

    @Override
    public ItemStack getStackRepresentation() {
        return ItemStack.EMPTY;
    }
}
