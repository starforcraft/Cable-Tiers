package com.YTrollman.CableTiers.registry;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.blocks.*;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CableTiers.MOD_ID);

    public static final RegistryObject<EliteExporterBlock> ELITE_EXPORTER = BLOCKS.register("elite_exporter", () -> new EliteExporterBlock());
    public static final RegistryObject<EliteConstructorBlock> ELITE_CONSTRUCTOR = BLOCKS.register("elite_constructor", () -> new EliteConstructorBlock());

    public static final RegistryObject<UltraExporterBlock> ULTRA_EXPORTER = BLOCKS.register("ultra_exporter", () -> new UltraExporterBlock());
    public static final RegistryObject<UltraConstructorBlock> ULTRA_CONSTRUCTOR = BLOCKS.register("ultra_constructor", () -> new UltraConstructorBlock());

    public static final RegistryObject<CreativeExporterBlock> CREATIVE_EXPORTER = BLOCKS.register("creative_exporter", () -> new CreativeExporterBlock());
    public static final RegistryObject<CreativeConstructorBlock> CREATIVE_CONSTRUCTOR = BLOCKS.register("creative_constructor", () -> new CreativeConstructorBlock());

}
