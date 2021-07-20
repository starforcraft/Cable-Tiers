package com.YTrollman.CableTiers.registry;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.tileentity.CreativeConstructorTileEntity;
import com.YTrollman.CableTiers.tileentity.CreativeDestructorTileEntity;
import com.YTrollman.CableTiers.tileentity.CreativeExporterTileEntity;
import com.YTrollman.CableTiers.tileentity.EliteConstructorTileEntity;
import com.YTrollman.CableTiers.tileentity.EliteDestructorTileEntity;
import com.YTrollman.CableTiers.tileentity.EliteExporterTileEntity;
import com.YTrollman.CableTiers.tileentity.UltraConstructorTileEntity;
import com.YTrollman.CableTiers.tileentity.UltraDestructorTileEntity;
import com.YTrollman.CableTiers.tileentity.UltraExporterTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.YTrollman.CableTiers.registry.RegistryHandler.TILE_ENTITY_TYPES;

public class ModTileEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CableTiers.MOD_ID);

    public static final RegistryObject<TileEntityType<EliteExporterTileEntity>> ELITE_EXPORTER_TILE_ENTITY = TILE_ENTITY_TYPES.register("elite_exporter", () -> TileEntityType.Builder
            .of(() -> new EliteExporterTileEntity(), ModBlocks.ELITE_EXPORTER.get())
            .build(null));
    public static final RegistryObject<TileEntityType<EliteConstructorTileEntity>> ELITE_CONSTRUCTOR_TILE_ENTITY = TILE_ENTITY_TYPES.register("elite_constructor", () -> TileEntityType.Builder
            .of(() -> new EliteConstructorTileEntity(), ModBlocks.ELITE_CONSTRUCTOR.get())
            .build(null));
    public static final RegistryObject<TileEntityType<EliteDestructorTileEntity>> ELITE_DESTRUCTOR_TILE_ENTITY = TILE_ENTITY_TYPES.register("elite_destructor", () -> TileEntityType.Builder
            .of(() -> new EliteDestructorTileEntity(), ModBlocks.ELITE_DESTRUCTOR.get())
            .build(null));
    
    public static final RegistryObject<TileEntityType<UltraExporterTileEntity>> ULTRA_EXPORTER_TILE_ENTITY = TILE_ENTITY_TYPES.register("ultra_exporter", () -> TileEntityType.Builder
            .of(() -> new UltraExporterTileEntity(), ModBlocks.ULTRA_EXPORTER.get())
            .build(null));
    public static final RegistryObject<TileEntityType<UltraConstructorTileEntity>> ULTRA_CONSTRUCTOR_TILE_ENTITY = TILE_ENTITY_TYPES.register("ultra_constructor", () -> TileEntityType.Builder
            .of(() -> new UltraConstructorTileEntity(), ModBlocks.ULTRA_CONSTRUCTOR.get())
            .build(null));
    public static final RegistryObject<TileEntityType<UltraDestructorTileEntity>> ULTRA_DESTRUCTOR_TILE_ENTITY = TILE_ENTITY_TYPES.register("ultra_destructor", () -> TileEntityType.Builder
            .of(() -> new UltraDestructorTileEntity(), ModBlocks.ULTRA_DESTRUCTOR.get())
            .build(null));
    
    public static final RegistryObject<TileEntityType<CreativeExporterTileEntity>> CREATIVE_EXPORTER_TILE_ENTITY = TILE_ENTITY_TYPES.register("creative_exporter", () -> TileEntityType.Builder
            .of(() -> new CreativeExporterTileEntity(), ModBlocks.CREATIVE_EXPORTER.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CreativeConstructorTileEntity>> CREATIVE_CONSTRUCTOR_TILE_ENTITY = TILE_ENTITY_TYPES.register("creative_constructor", () -> TileEntityType.Builder
            .of(() -> new CreativeConstructorTileEntity(), ModBlocks.CREATIVE_CONSTRUCTOR.get())
            .build(null));
    public static final RegistryObject<TileEntityType<CreativeDestructorTileEntity>> CREATIVE_DESTRUCTOR_TILE_ENTITY = TILE_ENTITY_TYPES.register("creative_destructor", () -> TileEntityType.Builder
            .of(() -> new CreativeDestructorTileEntity(), ModBlocks.CREATIVE_DESTRUCTOR.get())
            .build(null));
}
