package com.ultramega.cabletiers.fabric;

import com.ultramega.cabletiers.common.AbstractModInitializer;
import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.Platform;
import com.ultramega.cabletiers.common.packet.c2s.ChangeAdvancedResourceSlotPacket;
import com.ultramega.cabletiers.common.packet.c2s.RequestSidedResourcesPacket;
import com.ultramega.cabletiers.common.packet.c2s.SetAdvancedFilterPacket;
import com.ultramega.cabletiers.common.packet.c2s.SetSidedResourcesOnPatternGridBlockPacket;
import com.ultramega.cabletiers.common.packet.c2s.TieredAutocrafterNameChangePacket;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
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
        registerContent();
        registerPackets();
        registerPacketHandlers();
        registerCapabilities();
        registerCreativeModeTabListener(refinedStorageApi);
    }

    private void registerContent() {
        registerBlocks(new DirectRegistryCallback<>(BuiltInRegistries.BLOCK), BLOCK_ENTITY_PROVIDERS);
        registerItems(new DirectRegistryCallback<>(BuiltInRegistries.ITEM));
        registerUpgradeMappings();
        registerBlockEntities(
            new DirectRegistryCallback<>(BuiltInRegistries.BLOCK_ENTITY_TYPE),
            new BlockEntityTierTypeFactory() {
                @SuppressWarnings("DataFlowIssue") // data type can be null
                @Override
                public <T extends BlockEntity> BlockEntityType<T> create(final CableTiers tier,
                                                                         final BlockEntityTierProvider<T> factory,
                                                                         final Block... allowedBlocks) {
                    return new BlockEntityType<>((pos, state) -> factory.create(tier, pos, state), new HashSet<>(Arrays.asList(allowedBlocks)), null);
                }
            },
            new BlockEntityTypeFactory() {
                @SuppressWarnings("DataFlowIssue") // data type can be null
                @Override
                public <T extends BlockEntity> BlockEntityType<T> create(final BlockEntityProvider<T> factory,
                                                                         final Block... allowedBlocks) {
                    return new BlockEntityType<>(factory::create, new HashSet<>(Arrays.asList(allowedBlocks)), null);
                }
            },
            BLOCK_ENTITY_PROVIDERS
        );
        registerMenus(new DirectRegistryCallback<>(BuiltInRegistries.MENU), new ExtendedMenuTypeFactory() {
            @Override
            public <T extends AbstractContainerMenu, D> MenuType<T> create(final MenuSupplier<T, D> supplier,
                                                                           final StreamCodec<RegistryFriendlyByteBuf, D> streamCodec) {
                return new ExtendedScreenHandlerType<>(supplier::create, streamCodec);
            }
        });
        registerDataComponents(new DirectRegistryCallback<>(BuiltInRegistries.DATA_COMPONENT_TYPE));
    }

    private void registerCapabilities() {
        for (final CableTiers tier : CableTiers.values()) {
            registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredImporters(tier));
            registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredExporters(tier));
            registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredDestructors(tier));
            registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredConstructors(tier));
            registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredDiskInterfaces(tier));
            registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredAutocrafters(tier));
            registerNetworkNodeContainerProvider(BlockEntities.INSTANCE.getTieredInterfaces(tier));

            ItemStorage.SIDED.registerForBlockEntity((blockEntity, context) -> {
                final InventoryStorage storage = InventoryStorage.of(blockEntity.getDiskInventory(), context);
                final List<Storage<ItemVariant>> parts = new ArrayList<>();
                for (int i = 0; i < AbstractTieredDiskInterfaceBlockEntity.AMOUNT_OF_DISKS; ++i) {
                    final var slot = storage.getSlot(i);
                    parts.add(i < 3 ? FilteringStorage.insertOnlyOf(slot) : FilteringStorage.extractOnlyOf(slot));
                }
                return new CombinedStorage<>(parts);
            }, BlockEntities.INSTANCE.getTieredDiskInterfaces(tier));
        }
    }

    private void registerNetworkNodeContainerProvider(final BlockEntityType<? extends AbstractNetworkNodeContainerBlockEntity<?>> type) {
        RefinedStorageFabricApi.INSTANCE.getNetworkNodeContainerProviderLookup().registerForBlockEntity(
            (be, dir) -> be.getContainerProvider(),
            type
        );
    }

    private void registerCreativeModeTabListener(final RefinedStorageApi refinedStorageApi) {
        final ResourceKey<CreativeModeTab> creativeModeTab = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            refinedStorageApi.getCreativeModeTabId()
        );
        ItemGroupEvents.modifyEntriesEvent(creativeModeTab).register(
            entries -> CreativeModeTabItems.appendBlocks(entries::accept)
        );

        final ResourceKey<CreativeModeTab> coloredCreativeModeTab = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            refinedStorageApi.getColoredCreativeModeTabId()
        );
        ItemGroupEvents.modifyEntriesEvent(coloredCreativeModeTab).register(
            entries -> CreativeModeTabItems.appendColoredVariants(entries::accept)
        );
    }

    private void registerPackets() {
        registerServerToClientPackets();
        registerClientToServerPackets();
    }

    private void registerServerToClientPackets() {
        PayloadTypeRegistry.playS2C().register(ShouldOpenAdvancedFilterPacket.PACKET_TYPE, ShouldOpenAdvancedFilterPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateAdvancedFilterPacket.PACKET_TYPE, UpdateAdvancedFilterPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(TieredAutocrafterLockedUpdatePacket.PACKET_TYPE, TieredAutocrafterLockedUpdatePacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(TieredAutocrafterNameUpdatePacket.PACKET_TYPE, TieredAutocrafterNameUpdatePacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SetSidedResourcesOnPatternGridMenuPacket.PACKET_TYPE, SetSidedResourcesOnPatternGridMenuPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ReplaceSidedResourceOnPatternGridMenuPacket.PACKET_TYPE, ReplaceSidedResourceOnPatternGridMenuPacket.STREAM_CODEC);
    }

    private void registerClientToServerPackets() {
        PayloadTypeRegistry.playC2S().register(ChangeAdvancedResourceSlotPacket.PACKET_TYPE, ChangeAdvancedResourceSlotPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SetAdvancedFilterPacket.PACKET_TYPE, SetAdvancedFilterPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(TieredAutocrafterNameChangePacket.PACKET_TYPE, TieredAutocrafterNameChangePacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestSidedResourcesPacket.PACKET_TYPE, RequestSidedResourcesPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SetSidedResourcesOnPatternGridBlockPacket.PACKET_TYPE, SetSidedResourcesOnPatternGridBlockPacket.STREAM_CODEC);
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
