package com.YTrollman.CableTiers.registry;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.node.*;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = CableTiers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        API.instance().getNetworkNodeRegistry().add(EliteExporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new EliteExporterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(EliteConstructorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new EliteConstructorNetworkNode(world, pos)));
        ModTileEntityTypes.ELITE_EXPORTER_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        ModTileEntityTypes.ELITE_CONSTRUCTOR_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);

        API.instance().getNetworkNodeRegistry().add(UltraExporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new UltraExporterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(UltraConstructorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new UltraConstructorNetworkNode(world, pos)));
        ModTileEntityTypes.ULTRA_EXPORTER_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        ModTileEntityTypes.ULTRA_CONSTRUCTOR_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);

        API.instance().getNetworkNodeRegistry().add(CreativeExporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CreativeExporterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(CreativeConstructorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CreativeConstructorNetworkNode(world, pos)));
        ModTileEntityTypes.CREATIVE_EXPORTER_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        ModTileEntityTypes.CREATIVE_CONSTRUCTOR_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
    }

    private static INetworkNode readAndReturn(CompoundNBT tag, NetworkNode node) {
        node.read(tag);
        return node;
    }
}
