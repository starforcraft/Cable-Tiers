package com.YTrollman.CableTiers.registry;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.blocks.CreativeConstructorBlock;
import com.YTrollman.CableTiers.blocks.CreativeDestructorBlock;
import com.YTrollman.CableTiers.blocks.CreativeExporterBlock;
import com.YTrollman.CableTiers.blocks.CreativeImporterBlock;
import com.YTrollman.CableTiers.blocks.EliteConstructorBlock;
import com.YTrollman.CableTiers.blocks.EliteDestructorBlock;
import com.YTrollman.CableTiers.blocks.EliteExporterBlock;
import com.YTrollman.CableTiers.blocks.EliteImporterBlock;
import com.YTrollman.CableTiers.blocks.UltraConstructorBlock;
import com.YTrollman.CableTiers.blocks.UltraDestructorBlock;
import com.YTrollman.CableTiers.blocks.UltraExporterBlock;
import com.YTrollman.CableTiers.blocks.UltraImporterBlock;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CableTiers.MOD_ID);

    public static final RegistryObject<EliteImporterBlock> ELITE_IMPORTER = BLOCKS.register("elite_importer", () -> new EliteImporterBlock());
    public static final RegistryObject<EliteExporterBlock> ELITE_EXPORTER = BLOCKS.register("elite_exporter", () -> new EliteExporterBlock());
    public static final RegistryObject<EliteConstructorBlock> ELITE_CONSTRUCTOR = BLOCKS.register("elite_constructor", () -> new EliteConstructorBlock());
    public static final RegistryObject<EliteDestructorBlock> ELITE_DESTRUCTOR = BLOCKS.register("elite_destructor", () -> new EliteDestructorBlock());
    
    public static final RegistryObject<UltraImporterBlock> ULTRA_IMPORTER = BLOCKS.register("ultra_importer", () -> new UltraImporterBlock());
    public static final RegistryObject<UltraExporterBlock> ULTRA_EXPORTER = BLOCKS.register("ultra_exporter", () -> new UltraExporterBlock());
    public static final RegistryObject<UltraConstructorBlock> ULTRA_CONSTRUCTOR = BLOCKS.register("ultra_constructor", () -> new UltraConstructorBlock());
    public static final RegistryObject<UltraDestructorBlock> ULTRA_DESTRUCTOR = BLOCKS.register("ultra_destructor", () -> new UltraDestructorBlock());
    
    public static final RegistryObject<CreativeImporterBlock> CREATIVE_IMPORTER = BLOCKS.register("creative_importer", () -> new CreativeImporterBlock());
    public static final RegistryObject<CreativeExporterBlock> CREATIVE_EXPORTER = BLOCKS.register("creative_exporter", () -> new CreativeExporterBlock());
    public static final RegistryObject<CreativeConstructorBlock> CREATIVE_CONSTRUCTOR = BLOCKS.register("creative_constructor", () -> new CreativeConstructorBlock());
    public static final RegistryObject<CreativeDestructorBlock> CREATIVE_DESTRUCTOR = BLOCKS.register("creative_destructor", () -> new CreativeDestructorBlock());
}