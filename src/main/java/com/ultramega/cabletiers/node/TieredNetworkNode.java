package com.ultramega.cabletiers.node;

import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredBlockEntity;
import com.ultramega.cabletiers.config.CableConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public abstract class TieredNetworkNode<N extends TieredNetworkNode<N>> extends NetworkNode {
    private final ContentType<?, ? extends TieredBlockEntity<N>, ?, N> contentType;
    private final CableTier tier;
    private final ResourceLocation id;

    protected TieredNetworkNode(Level level, BlockPos pos, ContentType<?, ? extends TieredBlockEntity<N>, ?, N> contentType, CableTier tier) {
        super(level, pos);
        this.contentType = contentType;
        this.tier = tier;
        this.id = contentType.getId(tier);
    }

    public ContentType<?, ? extends TieredBlockEntity<N>, ?, N> getContentType() {
        return contentType;
    }

    public CableTier getTier() {
        return tier;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public int getAdditionalEnergyCost() {
        return switch (getTier()) {
            case ELITE -> CableConfig.ELITE_ENERGY_COST.get();
            case ULTRA -> CableConfig.ULTRA_ENERGY_COST.get();
            case MEGA -> CableConfig.MEGA_ENERGY_COST.get();
        };
    }

    public int getTieredStackInteractCount(UpgradeItemHandler upgrades) {
        return (int) ((getTier() == CableTier.ELITE ? (upgrades.hasUpgrade(UpgradeItem.Type.STACK) ? 64 : 1) : 64) * Math.pow(getTier().getSlotsMultiplier(), 3));
    }

    public int getTieredStackInteractCount() {
        return (int) (64 * Math.pow(getTier().getSlotsMultiplier(), 3));
    }

    public double getSpeedMultiplier(int type) {
        Map<Integer, Double> configMap = new HashMap<>();

        switch (getTier()) {
            case ELITE -> {
                configMap.put(0, CableConfig.ELITE_EXPORTER_SPEED.get());
                configMap.put(1, CableConfig.ELITE_IMPORTER_SPEED.get());
                configMap.put(2, CableConfig.ELITE_CONSTRUCTOR_SPEED.get());
                configMap.put(3, CableConfig.ELITE_DESTRUCTOR_SPEED.get());
                configMap.put(4, CableConfig.ELITE_DISK_MANIPULATOR_SPEED.get());
                configMap.put(5, CableConfig.ELITE_INTERFACE_SPEED.get());
            }
            case ULTRA -> {
                configMap.put(0, CableConfig.ULTRA_EXPORTER_SPEED.get());
                configMap.put(1, CableConfig.ULTRA_IMPORTER_SPEED.get());
                configMap.put(2, CableConfig.ULTRA_CONSTRUCTOR_SPEED.get());
                configMap.put(3, CableConfig.ULTRA_DESTRUCTOR_SPEED.get());
                configMap.put(4, CableConfig.ULTRA_DISK_MANIPULATOR_SPEED.get());
                configMap.put(5, CableConfig.ULTRA_INTERFACE_SPEED.get());
            }
            case MEGA -> {
                configMap.put(0, CableConfig.MEGA_EXPORTER_SPEED.get());
                configMap.put(1, CableConfig.MEGA_IMPORTER_SPEED.get());
                configMap.put(2, CableConfig.MEGA_CONSTRUCTOR_SPEED.get());
                configMap.put(3, CableConfig.MEGA_DESTRUCTOR_SPEED.get());
                configMap.put(4, CableConfig.MEGA_DISK_MANIPULATOR_SPEED.get());
                configMap.put(5, CableConfig.MEGA_INTERFACE_SPEED.get());
            }
        }

        return configMap.get(type);
    }
}
