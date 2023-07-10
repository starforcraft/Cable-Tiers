package com.ultramega.cabletiers;

import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.blockentity.BaseBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;
import com.ultramega.cabletiers.blockentity.*;
import com.ultramega.cabletiers.blocks.*;
import com.ultramega.cabletiers.container.*;
import com.ultramega.cabletiers.node.*;
import com.ultramega.cabletiers.node.diskmanipulator.TieredDiskManipulatorNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import static com.ultramega.cabletiers.registry.RegistryHandler.*;

@Mod.EventBusSubscriber(modid = CableTiers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ContentType<B extends BaseBlock, T extends TieredBlockEntity<N>, C extends TieredContainerMenu<T, N>, N extends TieredNetworkNode<N>> {
    public static final ContentType<TieredImporterBlock, TieredImporterBlockEntity, TieredImporterContainerMenu, TieredImporterNetworkNode> IMPORTER = new ContentType<>("importer", TieredImporterBlock::new, TieredImporterBlockEntity::new, TieredImporterContainerMenu::new, TieredImporterNetworkNode::new);
    public static final ContentType<TieredExporterBlock, TieredExporterBlockEntity, TieredExporterContainerMenu, TieredExporterNetworkNode> EXPORTER = new ContentType<>("exporter", TieredExporterBlock::new, TieredExporterBlockEntity::new, TieredExporterContainerMenu::new, TieredExporterNetworkNode::new);
    public static final ContentType<TieredConstructorBlock, TieredConstructorBlockEntity, TieredConstructorContainerMenu, TieredConstructorNetworkNode> CONSTRUCTOR = new ContentType<>("constructor", TieredConstructorBlock::new, TieredConstructorBlockEntity::new, TieredConstructorContainerMenu::new, TieredConstructorNetworkNode::new);
    public static final ContentType<TieredDestructorBlock, TieredDestructorBlockEntity, TieredDestructorContainerMenu, TieredDestructorNetworkNode> DESTRUCTOR = new ContentType<>("destructor", TieredDestructorBlock::new, TieredDestructorBlockEntity::new, TieredDestructorContainerMenu::new, TieredDestructorNetworkNode::new);
    public static final ContentType<TieredDiskManipulatorBlock, TieredDiskManipulatorBlockEntity, TieredDiskManipulatorContainer, TieredDiskManipulatorNetworkNode> DISK_MANIPULATOR = new ContentType<>("disk_manipulator", TieredDiskManipulatorBlock::new, TieredDiskManipulatorBlockEntity::new, TieredDiskManipulatorContainer::new, TieredDiskManipulatorNetworkNode::new);
    public static final ContentType<TieredRequesterBlock, TieredRequesterBlockEntity, TieredRequesterContainer, TieredRequesterNetworkNode> REQUESTER = new ContentType<>("requester", TieredRequesterBlock::new, TieredRequesterBlockEntity::new, TieredRequesterContainer::new, TieredRequesterNetworkNode::new);

    public static final ContentType<?, ?, ?, ?>[] CONTENT_TYPES = {EXPORTER, IMPORTER, CONSTRUCTOR, DESTRUCTOR, DISK_MANIPULATOR, REQUESTER};

    private final Map<CableTier, RegistryObject<B>> blocks = new EnumMap<>(CableTier.class);
    private final Map<CableTier, RegistryObject<Item>> items = new EnumMap<>(CableTier.class);
    private final Map<CableTier, RegistryObject<BlockEntityType<T>>> blockEntityTypes = new EnumMap<>(CableTier.class);
    private final Map<CableTier, RegistryObject<MenuType<C>>> containerTypes = new EnumMap<>(CableTier.class);

    private final String name;
    private final Function<CableTier, B> blockFactory;
    private final BlockEntityFactory<T> blockEntityFactory;
    private final MenuFactory<T, C> containerFactory;
    private final NetworkNodeFactory<N> networkNodeFactory;

    private ContentType(String name, Function<CableTier, B> blockFactory, BlockEntityFactory<T> blockEntityFactory, MenuFactory<T, C> containerFactory, NetworkNodeFactory<N> networkNodeFactory) {
        this.name = name;
        this.blockFactory = blockFactory;
        this.blockEntityFactory = blockEntityFactory;
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

    public BlockEntityType<T> getBlockEntityType(CableTier tier) {
        return blockEntityTypes.get(tier).get();
    }

    public MenuType<C> getContainerType(CableTier tier) {
        return containerTypes.get(tier).get();
    }

    public String getName(CableTier tier) {
        return tier.getName() + "_" + name;
    }

    public ResourceLocation getId(CableTier tier) {
        return new ResourceLocation(CableTiers.MOD_ID, getName(tier));
    }

    public C createContainer(int windowId, Player player, T tile) {
        return containerFactory.create(windowId, player, tile);
    }

    public N createNetworkNode(Level level, BlockPos pos, CableTier tier) {
        return networkNodeFactory.create(level, pos, tier);
    }

    private void initContent() {
        for (CableTier tier : CableTier.VALUES) {
            String id = getName(tier);
            blocks.put(tier, BLOCKS.register(id, () -> blockFactory.apply(tier)));
            items.put(tier, ITEMS.register(id, () -> new BaseBlockItem(getBlock(tier), new Item.Properties())));
            blockEntityTypes.put(tier, BLOCK_ENTITY_TYPES.register(id, () -> BlockEntityType.Builder.of((pos, state) -> blockEntityFactory.create(tier, pos, state), getBlock(tier)).build(null)));
            containerTypes.put(tier, CONTAINER_TYPES.register(id, () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                BlockEntity tile = inv.player.getCommandSenderWorld().getBlockEntity(pos);
                if (tile == null) {
                    CableTiers.LOGGER.error("Expected tile entity of type " + id + ", but found none");
                    return null;
                }

                BlockEntityType<T> blockEntityType = getBlockEntityType(tier);
                if (tile.getType() != blockEntityType) {
                    CableTiers.LOGGER.error("Wrong type of block entity, expected " + blockEntityType.toString() + ", but got " + tile.getType().toString());
                    return null;
                }

                return createContainer(windowId, inv.player, (T) tile);
            })));
        }
    }

    private void registerContent() {
        for (CableTier tier : CableTier.VALUES) {
            API.instance().getNetworkNodeRegistry().add(getId(tier), (tag, level, pos) -> {
                NetworkNode node = createNetworkNode(level, pos, tier);
                node.read(tag);
                return node;
            });
            getBlockEntityType(tier).create(BlockPos.ZERO, null).getDataManager().getParameters().forEach(BlockEntitySynchronizationManager::registerParameter);
        }
    }

    public static final RegistryObject<CreativeModeTab> TAB_CABLETIERS = TABS.register(CableTiers.MOD_ID, () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.cabletiers")).icon(() -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(CableTiers.MOD_ID, "mega_importer")))).displayItems((featureFlags, output) -> {
        for (ContentType<?, ?, ?, ?> type : CONTENT_TYPES) {
            for (CableTier tier : CableTier.VALUES) {
                output.accept(ForgeRegistries.ITEMS.getValue(new ResourceLocation(CableTiers.MOD_ID, type.getName(tier))));
            }
        }
    }).build());

    @FunctionalInterface
    private interface BlockEntityFactory<T extends BaseBlockEntity> {
        T create(CableTier tier, BlockPos pos, BlockState state);
    }

    @FunctionalInterface
    private interface MenuFactory<T extends BaseBlockEntity, C extends Container> {
        C create(int windowId, Player player, T tile);
    }

    @FunctionalInterface
    private interface NetworkNodeFactory<N> {
        N create(Level level, BlockPos pos, CableTier tier);
    }
}
