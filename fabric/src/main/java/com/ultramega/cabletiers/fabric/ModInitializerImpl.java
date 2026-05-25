package com.ultramega.cabletiers.fabric;

import com.ultramega.cabletiers.common.AbstractModInitializer;
import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.Platform;
import com.ultramega.cabletiers.common.iface.TieredInterfaceBlockEntity;
import com.ultramega.cabletiers.common.packet.c2s.ChangeAdvancedResourceSlotPacket;
import com.ultramega.cabletiers.common.packet.c2s.RequestSidedResourcesPacket;
import com.ultramega.cabletiers.common.packet.c2s.SetAdvancedFilterPacket;
import com.ultramega.cabletiers.common.packet.c2s.SetSidedResourcesOnPatternGridBlockPacket;
import com.ultramega.cabletiers.common.packet.c2s.TieredAutocrafterNameChangePacket;
import com.ultramega.cabletiers.common.packet.s2c.ClearSidedResourceOnPatternGridMenuPacket;
import com.ultramega.cabletiers.common.packet.s2c.ReplaceSidedResourceOnPatternGridMenuPacket;
import com.ultramega.cabletiers.common.packet.s2c.SetSidedResourcesOnPatternGridMenuPacket;
import com.ultramega.cabletiers.common.packet.s2c.ShouldOpenAdvancedFilterPacket;
import com.ultramega.cabletiers.common.packet.s2c.TieredAutocrafterLockedUpdatePacket;
import com.ultramega.cabletiers.common.packet.s2c.TieredAutocrafterNameUpdatePacket;
import com.ultramega.cabletiers.common.packet.s2c.UpdateAdvancedFilterPacket;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.registry.CreativeModeTabItems;
import com.ultramega.cabletiers.common.storage.diskinterface.AbstractTieredDiskInterfaceBlockEntity;
import com.ultramega.cabletiers.common.utils.BlockEntityProviders;
import com.ultramega.cabletiers.common.utils.BlockEntityTierProvider;
import com.ultramega.cabletiers.common.utils.BlockEntityTierTypeFactory;
import com.ultramega.cabletiers.fabric.constructordestructor.FabricTieredConstructorBlockEntity;
import com.ultramega.cabletiers.fabric.constructordestructor.FabricTieredDestructorBlockEntity;
import com.ultramega.cabletiers.fabric.exporter.FabricTieredExporterBlockEntity;
import com.ultramega.cabletiers.fabric.importer.FabricTieredImporterBlockEntity;
import com.ultramega.cabletiers.fabric.storage.diskinterface.FabricTieredDiskInterfaceBlockEntity;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.content.BlockEntityProvider;
import com.refinedmods.refinedstorage.common.content.BlockEntityTypeFactory;
import com.refinedmods.refinedstorage.common.content.DirectRegistryCallback;
import com.refinedmods.refinedstorage.common.content.ExtendedMenuTypeFactory;
import com.refinedmods.refinedstorage.common.support.packet.PacketHandler;
import com.refinedmods.refinedstorage.fabric.api.RefinedStorageFabricApi;
import com.refinedmods.refinedstorage.fabric.api.RefinedStoragePlugin;
import com.refinedmods.refinedstorage.fabric.support.resource.ResourceContainerFluidStorageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModInitializerImpl extends AbstractModInitializer implements RefinedStoragePlugin, ModInitializer {
    private static final BlockEntityProviders BLOCK_ENTITY_PROVIDERS = new BlockEntityProviders(
        FabricTieredImporterBlockEntity::new,
        FabricTieredExporterBlockEntity::new,
        FabricTieredDestructorBlockEntity::new,
        FabricTieredConstructorBlockEntity::new,
        FabricTieredDiskInterfaceBlockEntity::new
    );

    @Override
    public void onApiAvailable(final RefinedStorageApi refinedStorageApi) {
        Platform.setConfigProvider(ConfigImpl::get);
        this.registerContent();
        this.registerPackets();
        this.registerPacketHandlers();
        this.registerCapabilities();
        this.registerCreativeModeTabListener(refinedStorageApi);
    }

    private void registerContent() {
        this.registerBlocks(new DirectRegistryCallback<>(BuiltInRegistries.BLOCK), BLOCK_ENTITY_PROVIDERS);
        this.registerItems(new DirectRegistryCallback<>(BuiltInRegistries.ITEM));
        this.registerUpgradeMappings();
        this.registerBlockEntities(
            new DirectRegistryCallback<>(BuiltInRegistries.BLOCK_ENTITY_TYPE),
            new BlockEntityTierTypeFactory() {
                @Override
                public <T extends BlockEntity> BlockEntityType<T> create(final CableTiers tier,
                                                                         final BlockEntityTierProvider<T> factory,
                                                                         final Block... allowedBlocks) {
                    return FabricBlockEntityTypeBuilder.create((pos, state) -> factory.create(tier, pos, state), allowedBlocks).build();
                }
            },
            new BlockEntityTypeFactory() {
                @Override
                public <T extends BlockEntity> BlockEntityType<T> create(final BlockEntityProvider<T> factory,
                                                                         final Block... allowedBlocks) {
                    return FabricBlockEntityTypeBuilder.create(factory::create, allowedBlocks).build();
                }
            },
            BLOCK_ENTITY_PROVIDERS
        );
        this.registerMenus(new DirectRegistryCallback<>(BuiltInRegistries.MENU), new ExtendedMenuTypeFactory() {
            @Override
            public <T extends AbstractContainerMenu, D> MenuType<T> create(final MenuSupplier<T, D> supplier,
                                                                           final StreamCodec<RegistryFriendlyByteBuf, D> streamCodec) {
                return new ExtendedMenuType<>(supplier::create, streamCodec);
            }
        });
        this.registerDataComponents(new DirectRegistryCallback<>(BuiltInRegistries.DATA_COMPONENT_TYPE));
    }

    private void registerCapabilities() {
        for (final CableTiers tier : CableTiers.values()) {
            this.registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredImporters(tier));
            this.registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredExporters(tier));
            this.registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredDestructors(tier));
            this.registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredConstructors(tier));
            this.registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredDiskInterfaces(tier));
            this.registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredAutocrafters(tier));
            this.registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredInterfaces(tier));

            ItemStorage.SIDED.registerForBlockEntity((blockEntity, context) -> {
                final ContainerStorage storage = ContainerStorage.of(blockEntity.getDiskInventory(), context);
                final List<Storage<ItemVariant>> parts = new ArrayList<>();
                for (int i = 0; i < AbstractTieredDiskInterfaceBlockEntity.AMOUNT_OF_DISKS; ++i) {
                    final var slot = storage.getSlot(i);
                    parts.add(i < 3 ? FilteringStorage.insertOnlyOf(slot) : FilteringStorage.extractOnlyOf(slot));
                }
                return new CombinedStorage<>(parts);
            }, BlockEntities.INSTANCE.getTieredDiskInterfaces(tier));

            this.registerItemStorage(
                TieredInterfaceBlockEntity.class::isInstance,
                TieredInterfaceBlockEntity.class::cast,
                TieredInterfaceBlockEntity::getExportedResourcesAsContainer,
                BlockEntities.INSTANCE.getTieredInterfaces(tier)
            );
            FluidStorage.SIDED.registerForBlockEntity(
                (blockEntity, context) -> new ResourceContainerFluidStorageAdapter(blockEntity.getExportedResources()),
                BlockEntities.INSTANCE.getTieredInterfaces(tier)
            );
        }
    }

    private void registerNetworkNodeContainerProvider(final BlockEntityType<? extends AbstractNetworkNodeContainerBlockEntity<?>> type) {
        RefinedStorageFabricApi.INSTANCE.getNetworkNodeContainerProviderLookup().registerForBlockEntity(
            (be, dir) -> be.getContainerProvider(),
            type
        );
    }

    private <T extends BlockEntity> void registerItemStorage(final Predicate<BlockEntity> test,
                                                             final Function<BlockEntity, T> caster,
                                                             final Function<T, Container> containerSupplier,
                                                             final BlockEntityType<?> type) {
        ItemStorage.SIDED.registerForBlockEntities((blockEntity, context) -> {
            if (test.test(blockEntity)) {
                final T casted = caster.apply(blockEntity);
                return ContainerStorage.of(containerSupplier.apply(casted), context);
            }
            return null;
        }, type);
    }

    private void registerCreativeModeTabListener(final RefinedStorageApi refinedStorageApi) {
        final ResourceKey<CreativeModeTab> creativeModeTab = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            refinedStorageApi.getCreativeModeTabId()
        );
        CreativeModeTabEvents.modifyOutputEvent(creativeModeTab).register(
            entries -> CreativeModeTabItems.appendBlocks(entries::accept)
        );

        final ResourceKey<CreativeModeTab> coloredCreativeModeTab = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            refinedStorageApi.getColoredCreativeModeTabId()
        );
        CreativeModeTabEvents.modifyOutputEvent(coloredCreativeModeTab).register(
            entries -> CreativeModeTabItems.appendColoredVariants(entries::accept)
        );
    }

    private void registerPackets() {
        this.registerServerToClientPackets();
        this.registerClientToServerPackets();
    }

    private void registerServerToClientPackets() {
        PayloadTypeRegistry.clientboundPlay().register(ShouldOpenAdvancedFilterPacket.PACKET_TYPE, ShouldOpenAdvancedFilterPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(UpdateAdvancedFilterPacket.PACKET_TYPE, UpdateAdvancedFilterPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(TieredAutocrafterLockedUpdatePacket.PACKET_TYPE, TieredAutocrafterLockedUpdatePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(TieredAutocrafterNameUpdatePacket.PACKET_TYPE, TieredAutocrafterNameUpdatePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SetSidedResourcesOnPatternGridMenuPacket.PACKET_TYPE, SetSidedResourcesOnPatternGridMenuPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ReplaceSidedResourceOnPatternGridMenuPacket.PACKET_TYPE, ReplaceSidedResourceOnPatternGridMenuPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClearSidedResourceOnPatternGridMenuPacket.PACKET_TYPE, ClearSidedResourceOnPatternGridMenuPacket.STREAM_CODEC);
    }

    private void registerClientToServerPackets() {
        PayloadTypeRegistry.serverboundPlay().register(ChangeAdvancedResourceSlotPacket.PACKET_TYPE, ChangeAdvancedResourceSlotPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetAdvancedFilterPacket.PACKET_TYPE, SetAdvancedFilterPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(TieredAutocrafterNameChangePacket.PACKET_TYPE, TieredAutocrafterNameChangePacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(RequestSidedResourcesPacket.PACKET_TYPE, RequestSidedResourcesPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetSidedResourcesOnPatternGridBlockPacket.PACKET_TYPE, SetSidedResourcesOnPatternGridBlockPacket.STREAM_CODEC);
    }

    private void registerPacketHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(ChangeAdvancedResourceSlotPacket.PACKET_TYPE, wrapHandler(ChangeAdvancedResourceSlotPacket::handle));
        ServerPlayNetworking.registerGlobalReceiver(SetAdvancedFilterPacket.PACKET_TYPE, wrapHandler(SetAdvancedFilterPacket::handle));
        ServerPlayNetworking.registerGlobalReceiver(TieredAutocrafterNameChangePacket.PACKET_TYPE, wrapHandler(TieredAutocrafterNameChangePacket::handle));
        ServerPlayNetworking.registerGlobalReceiver(RequestSidedResourcesPacket.PACKET_TYPE, wrapHandler(RequestSidedResourcesPacket::handle));
        ServerPlayNetworking.registerGlobalReceiver(SetSidedResourcesOnPatternGridBlockPacket.PACKET_TYPE,
            wrapHandler(SetSidedResourcesOnPatternGridBlockPacket::handle));
    }

    private static <T extends CustomPacketPayload> ServerPlayNetworking.PlayPayloadHandler<T> wrapHandler(final PacketHandler<T> handler) {
        return (packet, ctx) -> handler.handle(packet, ctx::player);
    }

    @Override
    public void onInitialize() {
        AutoConfig.register(ConfigImpl.class, Toml4jConfigSerializer::new);
    }
}
