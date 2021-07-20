package com.YTrollman.CableTiers.registry;

import com.YTrollman.CableTiers.CableTiers;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CableTiers.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CableTiers.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CableTiers.MOD_ID);
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, CableTiers.MOD_ID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        CONTAINER_TYPES.register(bus);
        ModContainers.CONTAINER_TYPES.register(bus);
        TILE_ENTITY_TYPES.register(bus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(bus);
        BLOCKS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ITEMS.register(bus);
        ModItems.ITEMS.register(bus);
    }
}
