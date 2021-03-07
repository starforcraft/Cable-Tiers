package com.YTrollman.CableTiers.registry;

import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.container.CreativeConstructorContainer;
import com.YTrollman.CableTiers.container.CreativeDestructorContainer;
import com.YTrollman.CableTiers.container.CreativeExporterContainer;
import com.YTrollman.CableTiers.container.CreativeImporterContainer;
import com.YTrollman.CableTiers.container.EliteConstructorContainer;
import com.YTrollman.CableTiers.container.EliteDestructorContainer;
import com.YTrollman.CableTiers.container.EliteExporterContainer;
import com.YTrollman.CableTiers.container.EliteImporterContainer;
import com.YTrollman.CableTiers.container.UltraConstructorContainer;
import com.YTrollman.CableTiers.container.UltraDestructorContainer;
import com.YTrollman.CableTiers.container.UltraExporterContainer;
import com.YTrollman.CableTiers.container.UltraImporterContainer;
import com.YTrollman.CableTiers.tileentity.CreativeConstructorTileEntity;
import com.YTrollman.CableTiers.tileentity.CreativeDestructorTileEntity;
import com.YTrollman.CableTiers.tileentity.CreativeExporterTileEntity;
import com.YTrollman.CableTiers.tileentity.CreativeImporterTileEntity;
import com.YTrollman.CableTiers.tileentity.EliteConstructorTileEntity;
import com.YTrollman.CableTiers.tileentity.EliteDestructorTileEntity;
import com.YTrollman.CableTiers.tileentity.EliteExporterTileEntity;
import com.YTrollman.CableTiers.tileentity.EliteImporterTileEntity;
import com.YTrollman.CableTiers.tileentity.UltraConstructorTileEntity;
import com.YTrollman.CableTiers.tileentity.UltraDestructorTileEntity;
import com.YTrollman.CableTiers.tileentity.UltraExporterTileEntity;
import com.YTrollman.CableTiers.tileentity.UltraImporterTileEntity;

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
        TileEntity te = inv.player.getEntityWorld().getTileEntity(pos);
        if(!(te instanceof EliteExporterTileEntity))
        {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected EliteExporterTileEntity)!");
            return null;
        }
        return new EliteExporterContainer(windowId, inv.player, (EliteExporterTileEntity) te);
    }));
    public static final RegistryObject<ContainerType<EliteImporterContainer>> ELITE_IMPORTER_CONTAINER = CONTAINER_TYPES.register("elite_importer", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getEntityWorld().getTileEntity(pos);
        if(!(te instanceof EliteImporterTileEntity))
        {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected EliteImporterTileEntity)!");
            return null;
        }
        return new EliteImporterContainer(windowId, inv.player, (EliteImporterTileEntity) te);
    }));
    public static final RegistryObject<ContainerType<EliteConstructorContainer>> ELITE_CONSTRUCTOR_CONTAINER = CONTAINER_TYPES.register("elite_constructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getEntityWorld().getTileEntity(pos);
        if(!(te instanceof EliteConstructorTileEntity))
        {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected EliteConstructorTileEntity)!");
            return null;
        }
        return new EliteConstructorContainer((EliteConstructorTileEntity) te, inv.player, windowId);
    }));
    public static final RegistryObject<ContainerType<EliteDestructorContainer>> ELITE_DESTRUCTOR_CONTAINER = CONTAINER_TYPES.register("elite_destructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getEntityWorld().getTileEntity(pos);
        if(!(te instanceof EliteDestructorTileEntity))
        {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected EliteDestructorTileEntity)!");
            return null;
        }
        return new EliteDestructorContainer((EliteDestructorTileEntity) te, inv.player, windowId);
    }));
    
    public static final RegistryObject<ContainerType<UltraExporterContainer>> ULTRA_EXPORTER_CONTAINER = CONTAINER_TYPES.register("ultra_exporter", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getEntityWorld().getTileEntity(pos);
        if(!(te instanceof UltraExporterTileEntity))
        {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected UltraExporterTileEntity)!");
            return null;
        }
        return new UltraExporterContainer(windowId, inv.player, (UltraExporterTileEntity) te);
    }));
    public static final RegistryObject<ContainerType<UltraImporterContainer>> ULTRA_IMPORTER_CONTAINER = CONTAINER_TYPES.register("ultra_importer", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getEntityWorld().getTileEntity(pos);
        if(!(te instanceof UltraImporterTileEntity))
        {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected UltraImporterTileEntity)!");
            return null;
        }
        return new UltraImporterContainer(windowId, inv.player, (UltraImporterTileEntity) te);
    }));
    public static final RegistryObject<ContainerType<UltraConstructorContainer>> ULTRA_CONSTRUCTOR_CONTAINER = CONTAINER_TYPES.register("ultra_constructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getEntityWorld().getTileEntity(pos);
        if(!(te instanceof UltraConstructorTileEntity))
        {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected UltraConstructorTileEntity)!");
            return null;
        }
        return new UltraConstructorContainer((UltraConstructorTileEntity) te, inv.player, windowId);
    }));
    public static final RegistryObject<ContainerType<UltraDestructorContainer>> ULTRA_DESTRUCTOR_CONTAINER = CONTAINER_TYPES.register("ultra_destructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getEntityWorld().getTileEntity(pos);
        if(!(te instanceof UltraDestructorTileEntity))
        {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected UltraDestructorTileEntity)!");
            return null;
        }
        return new UltraDestructorContainer((UltraDestructorTileEntity) te, inv.player, windowId);
    }));
 
    public static final RegistryObject<ContainerType<CreativeExporterContainer>> CREATIVE_EXPORTER_CONTAINER = CONTAINER_TYPES.register("creative_exporter", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getEntityWorld().getTileEntity(pos);
        if(!(te instanceof CreativeExporterTileEntity))
        {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected CreativeExporterTileEntity)!");
            return null;
        }
        return new CreativeExporterContainer(windowId, inv.player, (CreativeExporterTileEntity) te);
    }));
    public static final RegistryObject<ContainerType<CreativeImporterContainer>> CREATIVE_IMPORTER_CONTAINER = CONTAINER_TYPES.register("creative_importer", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getEntityWorld().getTileEntity(pos);
        if(!(te instanceof CreativeImporterTileEntity))
        {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected CreativeImporterTileEntity)!");
            return null;
        }
        return new CreativeImporterContainer(windowId, inv.player, (CreativeImporterTileEntity) te);
    }));
    public static final RegistryObject<ContainerType<CreativeConstructorContainer>> CREATIVE_CONSTRUCTOR_CONTAINER = CONTAINER_TYPES.register("creative_constructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getEntityWorld().getTileEntity(pos);
        if(!(te instanceof CreativeConstructorTileEntity))
        {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected CreativeConstructorTileEntity)!");
            return null;
        }
        return new CreativeConstructorContainer((CreativeConstructorTileEntity) te, inv.player, windowId);
    }));
    public static final RegistryObject<ContainerType<CreativeDestructorContainer>> CREATIVE_DESTRUCTOR_CONTAINER = CONTAINER_TYPES.register("creative_destructor", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity te = inv.player.getEntityWorld().getTileEntity(pos);
        if(!(te instanceof CreativeDestructorTileEntity))
        {
            CableTiers.LOGGER.error("Wrong type of tile entity (expected CreativeDestructorTileEntity)!");
            return null;
        }
        return new CreativeDestructorContainer((CreativeDestructorTileEntity) te, inv.player, windowId);
    }));
}
