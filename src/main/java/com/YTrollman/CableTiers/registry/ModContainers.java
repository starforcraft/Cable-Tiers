package com.YTrollman.CableTiers.registry;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.container.*;
import com.YTrollman.CableTiers.tileentity.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, CableTiers.MOD_ID);

    public static final RegistryObject<ContainerType<EliteExporterContainer>> ELITE_EXPORTER_CONTAINER = CONTAINER_TYPES.register("elite_exporter", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(te instanceof EliteExporterTileEntity)) {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected EliteExporterTileEntity)!");
            return null;
        }
        return new EliteExporterContainer(windowId, inv.player, (EliteExporterTileEntity) te);
    }));
    public static final RegistryObject<ContainerType<EliteConstructorContainer>> ELITE_CONSTRUCTOR_CONTAINER = CONTAINER_TYPES.register("elite_constructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(te instanceof EliteConstructorTileEntity)) {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected EliteConstructorTileEntity)!");
            return null;
        }
        return new EliteConstructorContainer((EliteConstructorTileEntity) te, inv.player, windowId);
    }));
    public static final RegistryObject<ContainerType<EliteDestructorContainer>> ELITE_DESTRUCTOR_CONTAINER = CONTAINER_TYPES.register("elite_destructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(te instanceof EliteDestructorTileEntity)) {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected EliteDestructorTileEntity)!");
            return null;
        }
        return new EliteDestructorContainer((EliteDestructorTileEntity) te, inv.player, windowId);
    }));

    public static final RegistryObject<ContainerType<UltraExporterContainer>> ULTRA_EXPORTER_CONTAINER = CONTAINER_TYPES.register("ultra_exporter", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(te instanceof UltraExporterTileEntity)) {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected UltraExporterTileEntity)!");
            return null;
        }
        return new UltraExporterContainer(windowId, inv.player, (UltraExporterTileEntity) te);
    }));
    public static final RegistryObject<ContainerType<UltraConstructorContainer>> ULTRA_CONSTRUCTOR_CONTAINER = CONTAINER_TYPES.register("ultra_constructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(te instanceof UltraConstructorTileEntity)) {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected UltraConstructorTileEntity)!");
            return null;
        }
        return new UltraConstructorContainer((UltraConstructorTileEntity) te, inv.player, windowId);
    }));
    public static final RegistryObject<ContainerType<UltraDestructorContainer>> ULTRA_DESTRUCTOR_CONTAINER = CONTAINER_TYPES.register("ultra_destructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(te instanceof UltraDestructorTileEntity)) {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected UltraDestructorTileEntity)!");
            return null;
        }
        return new UltraDestructorContainer((UltraDestructorTileEntity) te, inv.player, windowId);
    }));

    public static final RegistryObject<ContainerType<CreativeExporterContainer>> CREATIVE_EXPORTER_CONTAINER = CONTAINER_TYPES.register("creative_exporter", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(te instanceof CreativeExporterTileEntity)) {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected CreativeExporterTileEntity)!");
            return null;
        }
        return new CreativeExporterContainer(windowId, inv.player, (CreativeExporterTileEntity) te);
    }));
    public static final RegistryObject<ContainerType<CreativeConstructorContainer>> CREATIVE_CONSTRUCTOR_CONTAINER = CONTAINER_TYPES.register("creative_constructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(te instanceof CreativeConstructorTileEntity)) {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected CreativeConstructorTileEntity)!");
            return null;
        }
        return new CreativeConstructorContainer((CreativeConstructorTileEntity) te, inv.player, windowId);
    }));
    public static final RegistryObject<ContainerType<CreativeDestructorContainer>> CREATIVE_DESTRUCTOR_CONTAINER = CONTAINER_TYPES.register("creative_destructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getCommandSenderWorld().getBlockEntity(pos);
        if (!(te instanceof CreativeDestructorTileEntity)) {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected CreativeDestructorTileEntity)!");
            return null;
        }
        return new CreativeDestructorContainer((CreativeDestructorTileEntity) te, inv.player, windowId);
    }));
}
