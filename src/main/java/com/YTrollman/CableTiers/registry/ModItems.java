package com.YTrollman.CableTiers.registry;

import com.YTrollman.CableTiers.CableTiers;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CableTiers.MOD_ID);

    public static final RegistryObject<Item> ELITE_EXPORTER_ITEM = ITEMS.register("elite_exporter", () -> new BaseBlockItem(ModBlocks.ELITE_EXPORTER.get(), new Item.Properties().tab(RS.MAIN_GROUP)));
    public static final RegistryObject<Item> ELITE_CONSTRUCTOR_ITEM = ITEMS.register("elite_constructor", () -> new BaseBlockItem(ModBlocks.ELITE_CONSTRUCTOR.get(), new Item.Properties().tab(RS.MAIN_GROUP)));
    public static final RegistryObject<Item> ELITE_DESTRUCTOR_ITEM = ITEMS.register("elite_destructor", () -> new BaseBlockItem(ModBlocks.ELITE_DESTRUCTOR.get(), new Item.Properties().tab(RS.MAIN_GROUP)));

    public static final RegistryObject<Item> ULTRA_EXPORTER_ITEM = ITEMS.register("ultra_exporter", () -> new BaseBlockItem(ModBlocks.ULTRA_EXPORTER.get(), new Item.Properties().tab(RS.MAIN_GROUP)));
    public static final RegistryObject<Item> ULTRA_CONSTRUCTOR_ITEM = ITEMS.register("ultra_constructor", () -> new BaseBlockItem(ModBlocks.ULTRA_CONSTRUCTOR.get(), new Item.Properties().tab(RS.MAIN_GROUP)));
    public static final RegistryObject<Item> ULTRA_DESTRUCTOR_ITEM = ITEMS.register("ultra_destructor", () -> new BaseBlockItem(ModBlocks.ULTRA_DESTRUCTOR.get(), new Item.Properties().tab(RS.MAIN_GROUP)));

    public static final RegistryObject<Item> CREATIVE_EXPORTER_ITEM = ITEMS.register("creative_exporter", () -> new BaseBlockItem(ModBlocks.CREATIVE_EXPORTER.get(), new Item.Properties().tab(RS.MAIN_GROUP)));
    public static final RegistryObject<Item> CREATIVE_CONSTRUCTOR_ITEM = ITEMS.register("creative_constructor", () -> new BaseBlockItem(ModBlocks.CREATIVE_CONSTRUCTOR.get(), new Item.Properties().tab(RS.MAIN_GROUP)));
    public static final RegistryObject<Item> CREATIVE_DESTRUCTOR_ITEM = ITEMS.register("creative_destructor", () -> new BaseBlockItem(ModBlocks.CREATIVE_DESTRUCTOR.get(), new Item.Properties().tab(RS.MAIN_GROUP)));
}
