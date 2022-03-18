package com.YTrollman.CableTiers;

import com.YTrollman.CableTiers.blocks.*;
import com.YTrollman.CableTiers.container.*;
import com.YTrollman.CableTiers.node.*;
import com.YTrollman.CableTiers.tileentity.*;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;
import com.refinedmods.refinedstorage.tile.BaseTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import static com.YTrollman.CableTiers.registry.RegistryHandler.*;

@Mod.EventBusSubscriber(modid = CableTiers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ContentType<B extends BaseBlock, T extends TieredTileEntity<N>, C extends TieredContainer<T, N>, N extends TieredNetworkNode<N>> {

    public static final ContentType<TieredImporterBlock, TieredImporterTileEntity, TieredImporterContainer, TieredImporterNetworkNode> IMPORTER = new ContentType<>(
            "importer",
            TieredImporterBlock::new,
            TieredImporterTileEntity::new,
            TieredImporterContainer::new,
            TieredImporterNetworkNode::new
    );
    public static final ContentType<TieredExporterBlock, TieredExporterTileEntity, TieredExporterContainer, TieredExporterNetworkNode> EXPORTER = new ContentType<>(
            "exporter",
            TieredExporterBlock::new,
            TieredExporterTileEntity::new,
            TieredExporterContainer::new,
            TieredExporterNetworkNode::new
    );
    public static final ContentType<TieredConstructorBlock, TieredConstructorTileEntity, TieredConstructorContainer, TieredConstructorNetworkNode> CONSTRUCTOR = new ContentType<>(
            "constructor",
            TieredConstructorBlock::new,
            TieredConstructorTileEntity::new,
            TieredConstructorContainer::new,
            TieredConstructorNetworkNode::new
    );
    public static final ContentType<TieredDestructorBlock, TieredDestructorTileEntity, TieredDestructorContainer, TieredDestructorNetworkNode> DESTRUCTOR = new ContentType<>(
            "destructor",
            TieredDestructorBlock::new,
            TieredDestructorTileEntity::new,
            TieredDestructorContainer::new,
            TieredDestructorNetworkNode::new
    );
    public static final ContentType<TieredDiskManipulatorBlock, TieredDiskManipulatorTileEntity, TieredDiskManipulatorContainer, TieredDiskManipulatorNetworkNode> DISK_MANIPULATOR = new ContentType<>(
            "disk_manipulator",
            TieredDiskManipulatorBlock::new,
            TieredDiskManipulatorTileEntity::new,
            TieredDiskManipulatorContainer::new,
            TieredDiskManipulatorNetworkNode::new
    );
    public static final ContentType<TieredRequesterBlock, TieredRequesterTileEntity, TieredRequesterContainer, TieredRequesterNetworkNode> REQUESTER = new ContentType<>(
            "requester",
            TieredRequesterBlock::new,
            TieredRequesterTileEntity::new,
            TieredRequesterContainer::new,
            TieredRequesterNetworkNode::new
    );

    public static final ContentType<?, ?, ?, ?>[] CONTENT_TYPES = { EXPORTER, IMPORTER, CONSTRUCTOR, DESTRUCTOR, DISK_MANIPULATOR, REQUESTER };

    private final Map<CableTier, RegistryObject<B>> blocks = new EnumMap<>(CableTier.class);
    private final Map<CableTier, RegistryObject<Item>> items = new EnumMap<>(CableTier.class);
    private final Map<CableTier, RegistryObject<TileEntityType<T>>> tileEntityTypes = new EnumMap<>(CableTier.class);
    private final Map<CableTier, RegistryObject<ContainerType<C>>> containerTypes = new EnumMap<>(CableTier.class);

    private final String name;
    private final Function<CableTier, B> blockFactory;
    private final Function<CableTier, T> tileEntityFactory;
    private final ContainerFactory<T, C> containerFactory;
    private final NetworkNodeFactory<N> networkNodeFactory;

    private ContentType(String name, Function<CableTier, B> blockFactory, Function<CableTier, T> tileEntityFactory, ContainerFactory<T, C> containerFactory, NetworkNodeFactory<N> networkNodeFactory) {
        this.name = name;
        this.blockFactory = blockFactory;
        this.tileEntityFactory = tileEntityFactory;
        this.containerFactory = containerFactory;
        this.networkNodeFactory = networkNodeFactory;
    }

    public static void init() {
        for (ContentType<?, ?, ?, ?> type : ContentType.CONTENT_TYPES) {
            type.initContent();
        }
    }

    @SubscribeEvent
    public static void registerWithRS(FMLCommonSetupEvent event) {
        for (ContentType<?, ?, ?, ?> type : CONTENT_TYPES) {
            type.registerContent();
        }
    }

    public B getBlock(CableTier tier) {
        return blocks.get(tier).get();
    }

    public Item getItem(CableTier tier) {
        return items.get(tier).get();
    }

    public TileEntityType<T> getTileEntityType(CableTier tier) {
        return tileEntityTypes.get(tier).get();
    }

    public ContainerType<C> getContainerType(CableTier tier) {
        return containerTypes.get(tier).get();
    }

    public String getName(CableTier tier) {
        return tier.getName() + "_" + name;
    }

    public ResourceLocation getId(CableTier tier) {
        return new ResourceLocation(CableTiers.MOD_ID, getName(tier));
    }

    public C createContainer(int windowId, PlayerEntity player, T tile) {
        return containerFactory.create(windowId, player, tile);
    }

    public N createNetworkNode(World world, BlockPos pos, CableTier tier) {
        return networkNodeFactory.create(world, pos, tier);
    }

    private void initContent() {
        for (CableTier tier : CableTier.VALUES) {
            String id = getName(tier);
            blocks.put(tier, BLOCKS.register(id, () -> blockFactory.apply(tier)));
            items.put(tier, ITEMS.register(id, () -> new BaseBlockItem(getBlock(tier), new Item.Properties().tab(CABLE_TIERS))));
            tileEntityTypes.put(tier, TILE_ENTITY_TYPES.register(id, () -> TileEntityType.Builder.of(() -> tileEntityFactory.apply(tier), getBlock(tier)).build(null)));
            containerTypes.put(tier, CONTAINER_TYPES.register(id, () -> IForgeContainerType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        TileEntity tile = inv.player.getCommandSenderWorld().getBlockEntity(pos);
                        if (tile == null) {
                            CableTiers.LOGGER.error("Expected tile entity of type " + id + ", but found none");
                            return null;
                        }

                        TileEntityType<T> tileEntityType = getTileEntityType(tier);
                        if (tile.getType() != tileEntityType) {
                            CableTiers.LOGGER.error("Wrong type of tile entity, expected " + tileEntityType.getRegistryName() + ", but got " + tile.getType().getRegistryName());
                            return null;
                        }

                        return createContainer(windowId, inv.player, (T) tile);
                    })
            ));
        }
    }

    private void registerContent() {
        for (CableTier tier : CableTier.VALUES) {
            API.instance().getNetworkNodeRegistry().add(getId(tier), (tag, world, pos) -> {
                NetworkNode node = createNetworkNode(world, pos, tier);
                node.read(tag);
                return node;
            });
            getTileEntityType(tier).create().getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        }
    }

    @FunctionalInterface
    private interface ContainerFactory<T extends BaseTile, C extends Container> {
        C create(int windowId, PlayerEntity player, T tile);
    }

    @FunctionalInterface
    private interface NetworkNodeFactory<N> {
        N create(World world, BlockPos pos, CableTier tier);
    }

    public static final ItemGroup CABLE_TIERS = (new ItemGroup(CableTiers.MOD_ID) {

        @Override
        @Nonnull
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cabletiers:creative_importer")));
        }
    });
}
