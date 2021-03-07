package com.YTrollman.CableTiers.registry;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.node.CreativeConstructorNetworkNode;
import com.YTrollman.CableTiers.node.CreativeDestructorNetworkNode;
import com.YTrollman.CableTiers.node.CreativeExporterNetworkNode;
import com.YTrollman.CableTiers.node.CreativeImporterNetworkNode;
import com.YTrollman.CableTiers.node.EliteConstructorNetworkNode;
import com.YTrollman.CableTiers.node.EliteDestructorNetworkNode;
import com.YTrollman.CableTiers.node.EliteExporterNetworkNode;
import com.YTrollman.CableTiers.node.EliteImporterNetworkNode;
import com.YTrollman.CableTiers.node.UltraConstructorNetworkNode;
import com.YTrollman.CableTiers.node.UltraDestructorNetworkNode;
import com.YTrollman.CableTiers.node.UltraExporterNetworkNode;
import com.YTrollman.CableTiers.node.UltraImporterNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = CableTiers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {

    public static void init(final FMLCommonSetupEvent event)
    {
        API.instance().getNetworkNodeRegistry().add(EliteExporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new EliteExporterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(EliteImporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new EliteImporterNetworkNode(world, pos)));
        ModTileEntityTypes.ELITE_EXPORTER_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        ModTileEntityTypes.ELITE_IMPORTER_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        
        API.instance().getNetworkNodeRegistry().add(EliteConstructorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new EliteConstructorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(EliteDestructorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new EliteDestructorNetworkNode(world, pos)));
        ModTileEntityTypes.ELITE_CONSTRUCTOR_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        ModTileEntityTypes.ELITE_DESTRUCTOR_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        
        API.instance().getNetworkNodeRegistry().add(UltraExporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new UltraExporterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(UltraImporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new UltraImporterNetworkNode(world, pos)));
        ModTileEntityTypes.ULTRA_EXPORTER_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        ModTileEntityTypes.ULTRA_IMPORTER_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        
        API.instance().getNetworkNodeRegistry().add(UltraConstructorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new UltraConstructorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(UltraDestructorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new UltraDestructorNetworkNode(world, pos)));
        ModTileEntityTypes.ULTRA_CONSTRUCTOR_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        ModTileEntityTypes.ULTRA_DESTRUCTOR_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        
        API.instance().getNetworkNodeRegistry().add(CreativeExporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CreativeExporterNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(CreativeImporterNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CreativeImporterNetworkNode(world, pos)));
        ModTileEntityTypes.CREATIVE_EXPORTER_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        ModTileEntityTypes.CREATIVE_IMPORTER_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        
        API.instance().getNetworkNodeRegistry().add(CreativeConstructorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CreativeConstructorNetworkNode(world, pos)));
        API.instance().getNetworkNodeRegistry().add(CreativeDestructorNetworkNode.ID, (tag, world, pos) -> readAndReturn(tag, new CreativeDestructorNetworkNode(world, pos)));
        ModTileEntityTypes.CREATIVE_CONSTRUCTOR_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        ModTileEntityTypes.CREATIVE_DESTRUCTOR_TILE_ENTITY.get().create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
    }

    private static INetworkNode readAndReturn(CompoundNBT tag, NetworkNode node) {
        node.read(tag);
        return node;
    }
}
