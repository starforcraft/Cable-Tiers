package com.ultramega.cabletiers.common;

import com.ultramega.cabletiers.common.registry.Tags;
import com.ultramega.cabletiers.common.utils.ContentIds;
import com.ultramega.cabletiers.common.utils.ContentNames;

import java.util.Arrays;
import java.util.Locale;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public enum CableTiers implements StringRepresentable {
    ELITE(18, 18, 73),
    ULTRA(36, 27, 109),
    MEGA(54, 27, 145),
    CREATIVE(54, 27, 145);

    public static final Codec<CableTiers> CODEC = StringRepresentable.fromEnum(CableTiers::values);

    private final int filterSlotCount;
    private final int interfaceSlotsCount;
    private final int playerInventoryY;

    CableTiers(final int filterSlotsCount,
               final int interfaceSlotsCount,
               final int playerInventoryY) {
        this.filterSlotCount = filterSlotsCount;
        this.interfaceSlotsCount = interfaceSlotsCount;
        this.playerInventoryY = playerInventoryY;
    }

    public Identifier getContentId(final CableType type) {
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
            case AUTOCRAFTER -> Platform.getConfig().getTieredAutocrafters().getSpeed(this);
            default -> 0;
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
            case INTERFACE -> Platform.getConfig().getTieredInterfaces().getEnergyUsage(this);
        };
    }

    public long getTransferQuotaMultiplier(final CableType type) {
        if (type == CableType.INTERFACE) {
            return Platform.getConfig().getTieredInterfaces().getTransferQuotaMultiplier(this);
        }

        return 1L;
    }

    public int getFilterSlotCount() {
        return this.filterSlotCount;
    }

    public int getAutocrafterPatternSlotCount() {
        if (this == ELITE) {
            return this.getFilterSlotCount();
        }
        return Platform.getConfig().getTieredAutocrafters().getPatternSlotCount(this);
    }

    public int getInterfaceSlotsCount() {
        return this.interfaceSlotsCount;
    }

    public int getPlayerInventoryY() {
        return this.playerInventoryY;
    }

    public String getLowercaseName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    @Nullable
    public static CableTiers byName(final String search) {
        return Arrays.stream(CableTiers.values())
            .filter(each -> each.name().equalsIgnoreCase(search))
            .findFirst()
            .orElse(null);
    }

    @Override
    public String getSerializedName() {
        return this.getLowercaseName();
    }
}
