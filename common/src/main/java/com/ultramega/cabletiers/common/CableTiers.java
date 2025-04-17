package com.ultramega.cabletiers.common;

import com.ultramega.cabletiers.common.registry.Tags;
import com.ultramega.cabletiers.common.utils.ContentIds;
import com.ultramega.cabletiers.common.utils.ContentNames;

import java.util.Arrays;
import javax.annotation.Nullable;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public enum CableTiers {
    ELITE(18, 73),
    ULTRA(36, 109),
    MEGA(54, 145),
    CREATIVE(54, 145);

    private final int filterSlotsCount;
    private final int playerInventoryY;

    CableTiers(final int filterSlotsCount,
               final int playerInventoryY) {
        this.filterSlotsCount = filterSlotsCount;
        this.playerInventoryY = playerInventoryY;
    }

    public ResourceLocation getContentId(final CableType type) {
        return ContentIds.getContentId(this, type);
    }

    public MutableComponent getContentName(final CableType type) {
        return ContentNames.getContentName(this, type);
    }

    public TagKey<Item> getItemTag(final CableType type) {
        return Tags.getItemTag(this, type);
    }

    public TagKey<Block> getBlockTag(final CableType type) {
        return Tags.getBlockTag(this, type);
    }

    public int getSpeed(final CableType type) {
        return switch (type) {
            case IMPORTER -> Platform.getConfig().getTieredImporters().getSpeed(this);
            case EXPORTER -> Platform.getConfig().getTieredExporters().getSpeed(this);
            case DESTRUCTOR -> Platform.getConfig().getTieredDestructors().getSpeed(this);
            case CONSTRUCTOR -> Platform.getConfig().getTieredConstructors().getSpeed(this);
            case DISK_INTERFACE -> Platform.getConfig().getTieredDiskInterfaces().getSpeed(this);
            case AUTOCRAFTER -> Platform.getConfig().getTieredAutocrafters().getSpeed(this);
        };
    }

    public boolean hasIntegratedStackUpgrade(final CableType type) {
        return switch (type) {
            case IMPORTER -> Platform.getConfig().getTieredImporters().hasStackUpgradeIntegrated(this);
            case EXPORTER -> Platform.getConfig().getTieredExporters().hasStackUpgradeIntegrated(this);
            case CONSTRUCTOR -> Platform.getConfig().getTieredConstructors().hasStackUpgradeIntegrated(this);
            case DISK_INTERFACE -> Platform.getConfig().getTieredDiskInterfaces().hasStackUpgradeIntegrated(this);
            default -> false;
        };
    }

    public long getEnergyUsage(final CableType type) {
        return switch (type) {
            case IMPORTER -> Platform.getConfig().getTieredImporters().getEnergyUsage(this);
            case EXPORTER -> Platform.getConfig().getTieredExporters().getEnergyUsage(this);
            case DESTRUCTOR -> Platform.getConfig().getTieredDestructors().getEnergyUsage(this);
            case CONSTRUCTOR -> Platform.getConfig().getTieredConstructors().getEnergyUsage(this);
            case DISK_INTERFACE -> Platform.getConfig().getTieredDiskInterfaces().getEnergyUsage(this);
            case AUTOCRAFTER -> Platform.getConfig().getTieredAutocrafters().getEnergyUsage(this);
        };
    }

    public int getFilterSlotsCount() {
        return filterSlotsCount;
    }

    public int getPlayerInventoryY() {
        return playerInventoryY;
    }

    @Nullable
    public static CableTiers byName(final String search) {
        return Arrays.stream(CableTiers.values())
            .filter(each -> each.name().equalsIgnoreCase(search))
            .findFirst()
            .orElse(null);
    }
}
