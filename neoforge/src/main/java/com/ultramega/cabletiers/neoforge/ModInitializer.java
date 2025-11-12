package com.ultramega.cabletiers.neoforge;

import com.ultramega.cabletiers.common.AbstractModInitializer;
import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.Platform;
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
import com.ultramega.cabletiers.common.utils.TagsCache;
import com.ultramega.cabletiers.neoforge.capability.ImprovedInvWrapper;
import com.ultramega.cabletiers.neoforge.capability.ImprovedResourceContainerFluidHandlerAdapter;
import com.ultramega.cabletiers.neoforge.compat.ArsNouveauIntegration;
import com.ultramega.cabletiers.neoforge.compat.IndustrialForegoingSoulsIntegration;
import com.ultramega.cabletiers.neoforge.compat.MekanismIntegration;
import com.ultramega.cabletiers.neoforge.compat.RefinedTypesIntegration;
import com.ultramega.cabletiers.neoforge.constructordestructor.ForgeTieredConstructorBlockEntity;
import com.ultramega.cabletiers.neoforge.constructordestructor.ForgeTieredDestructorBlockEntity;
import com.ultramega.cabletiers.neoforge.exporter.ForgeTieredExporterBlockEntity;
import com.ultramega.cabletiers.neoforge.importer.ForgeTieredImporterBlockEntity;
import com.ultramega.cabletiers.neoforge.storage.diskinterface.ForgeTieredDiskInterfaceBlockEntity;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.content.BlockEntityProvider;
import com.refinedmods.refinedstorage.common.content.BlockEntityTypeFactory;
import com.refinedmods.refinedstorage.common.content.ExtendedMenuTypeFactory;
import com.refinedmods.refinedstorage.common.content.RegistryCallback;
import com.refinedmods.refinedstorage.common.support.packet.PacketHandler;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
import com.refinedmods.refinedstorage.neoforge.support.inventory.InsertExtractItemHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Supplier;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.MOD_ID;

@Mod(MOD_ID)
public class ModInitializer extends AbstractModInitializer {
    private static final BlockEntityProviders BLOCK_ENTITY_PROVIDERS = new BlockEntityProviders(
        ForgeTieredImporterBlockEntity::new,
        ForgeTieredExporterBlockEntity::new,
        ForgeTieredDestructorBlockEntity::new,
        ForgeTieredConstructorBlockEntity::new,
        ForgeTieredDiskInterfaceBlockEntity::new
    );

    private final DeferredRegister<Item> itemRegistry = DeferredRegister.create(BuiltInRegistries.ITEM, MOD_ID);
    private final DeferredRegister<Block> blockRegistry = DeferredRegister.create(BuiltInRegistries.BLOCK, MOD_ID);
    private final DeferredRegister<BlockEntityType<?>> blockEntityTypeRegistry = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MOD_ID);
    private final DeferredRegister<MenuType<?>> menuTypeRegistry = DeferredRegister.create(BuiltInRegistries.MENU, MOD_ID);
    private final DeferredRegister<DataComponentType<?>> dataComponentTypeRegistry = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MOD_ID);

    public ModInitializer(final IEventBus eventBus, final ModContainer modContainer) {
        final ConfigImpl config = new ConfigImpl();
        modContainer.registerConfig(ModConfig.Type.COMMON, config.getSpec());
        Platform.setConfigProvider(() -> config);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
            eventBus.addListener(ClientModInitializer::onClientSetup);
            eventBus.addListener(ClientModInitializer::onRegisterCustomModels);
            eventBus.addListener(ClientModInitializer::onRegisterMenuScreens);
        }

        eventBus.addListener(this::onCommonSetup);
        registerContent(eventBus);
        eventBus.addListener(this::registerCapabilities);
        eventBus.addListener(this::registerPackets);
        eventBus.addListener(this::registerCreativeModeTabListener);
        NeoForge.EVENT_BUS.addListener(this::onTagsUpdated);
    }

    private void registerContent(final IEventBus eventBus) {
        registerBlocks(eventBus);
        registerItems(eventBus);
        registerBlockEntities(eventBus);
        registerMenus(eventBus);
        registerDataComponents(eventBus);
    }

    private void registerBlocks(final IEventBus eventBus) {
        final RegistryCallback<Block> callback = new ForgeRegistryCallback<>(blockRegistry);
        registerBlocks(callback, BLOCK_ENTITY_PROVIDERS);
        blockRegistry.register(eventBus);
    }

    private void registerItems(final IEventBus eventBus) {
        final RegistryCallback<Item> callback = new ForgeRegistryCallback<>(itemRegistry);
        registerItems(callback);
        itemRegistry.register(eventBus);
    }

    private void registerBlockEntities(final IEventBus eventBus) {
        registerBlockEntities(
            new ForgeRegistryCallback<>(blockEntityTypeRegistry),
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
        blockEntityTypeRegistry.register(eventBus);
    }

    private void registerMenus(final IEventBus eventBus) {
        registerMenus(new ForgeRegistryCallback<>(menuTypeRegistry), new ExtendedMenuTypeFactory() {
            @Override
            public <T extends AbstractContainerMenu, D> MenuType<T> create(final MenuSupplier<T, D> supplier,
                                                                           final StreamCodec<RegistryFriendlyByteBuf, D> streamCodec) {
                return IMenuTypeExtension.create((syncId, inventory, buf) -> {
                    final D data = streamCodec.decode(buf);
                    return supplier.create(syncId, inventory, data);
                });
            }
        });
        menuTypeRegistry.register(eventBus);
    }

    private void registerDataComponents(final IEventBus eventBus) {
        final RegistryCallback<DataComponentType<?>> callback = new ForgeRegistryCallback<>(dataComponentTypeRegistry);
        registerDataComponents(callback);
        dataComponentTypeRegistry.register(eventBus);
    }

    private void onCommonSetup(final FMLCommonSetupEvent e) {
        registerUpgradeMappings();
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        final boolean hasRSMekanismIntegration = ModList.get().isLoaded("refinedstorage_mekanism_integration");
        final boolean hasRefinedTypes = ModList.get().isLoaded("refinedtypes");
        final boolean hasArsNouveau = ModList.get().isLoaded("ars_nouveau");
        final boolean hasIFSouls = ModList.get().isLoaded("industrialforegoingsouls");

        for (final CableTiers tier : CableTiers.values()) {
            registerNetworkNodeContainerProvider(event, BlockEntities.INSTANCE.getTieredImporters(tier));
            registerNetworkNodeContainerProvider(event, BlockEntities.INSTANCE.getTieredExporters(tier));
            registerNetworkNodeContainerProvider(event, BlockEntities.INSTANCE.getTieredDestructors(tier));
            registerNetworkNodeContainerProvider(event, BlockEntities.INSTANCE.getTieredConstructors(tier));
            registerNetworkNodeContainerProvider(event, BlockEntities.INSTANCE.getTieredDiskInterfaces(tier));
            registerNetworkNodeContainerProvider(event, BlockEntities.INSTANCE.getTieredAutocrafters(tier));
            registerNetworkNodeContainerProvider(event, BlockEntities.INSTANCE.getTieredInterfaces(tier));

            event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BlockEntities.INSTANCE.getTieredDiskInterfaces(tier),
                (be, side) -> {
                    final InvWrapper wrapper = new InvWrapper(be.getDiskInventory());
                    return new InsertExtractItemHandler(
                        new RangedWrapper(
                            wrapper,
                            0,
                            AbstractTieredDiskInterfaceBlockEntity.AMOUNT_OF_DISKS / 2
                        ),
                        new RangedWrapper(
                            wrapper,
                            AbstractTieredDiskInterfaceBlockEntity.AMOUNT_OF_DISKS / 2,
                            AbstractTieredDiskInterfaceBlockEntity.AMOUNT_OF_DISKS
                        )
                    );
                }
            );

            event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BlockEntities.INSTANCE.getTieredInterfaces(tier),
                (be, side) -> new ImprovedInvWrapper(be.getExportedResourcesAsContainer())
            );
            event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BlockEntities.INSTANCE.getTieredInterfaces(tier),
                (be, side) -> new ImprovedResourceContainerFluidHandlerAdapter(be.getExportedResources())
            );
            if (hasRSMekanismIntegration) {
                MekanismIntegration.registerCapabilities(tier, event);
            }
            if (hasRefinedTypes) {
                RefinedTypesIntegration.registerCapabilities(tier, event);
                if (hasArsNouveau) {
                    ArsNouveauIntegration.registerCapabilities(tier, event);
                }
                if (hasIFSouls) {
                    IndustrialForegoingSoulsIntegration.registerCapabilities(tier, event);
                }
            }
        }
    }

    private void registerNetworkNodeContainerProvider(
        final RegisterCapabilitiesEvent event,
        final BlockEntityType<? extends AbstractNetworkNodeContainerBlockEntity<?>> type
    ) {
        event.registerBlockEntity(
            RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
            type,
            (be, side) -> be.getContainerProvider()
        );
    }

    private void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MOD_ID);
        registerServerToClientPackets(registrar);
        registerClientToServerPackets(registrar);
    }

    private static void registerServerToClientPackets(final PayloadRegistrar registrar) {
        registrar.playToClient(
            ShouldOpenAdvancedFilterPacket.PACKET_TYPE,
            ShouldOpenAdvancedFilterPacket.STREAM_CODEC,
            wrapHandler(ShouldOpenAdvancedFilterPacket::handle)
        );
        registrar.playToClient(
            UpdateAdvancedFilterPacket.PACKET_TYPE,
            UpdateAdvancedFilterPacket.STREAM_CODEC,
            wrapHandler(UpdateAdvancedFilterPacket::handle)
        );
        registrar.playToClient(
            TieredAutocrafterLockedUpdatePacket.PACKET_TYPE,
            TieredAutocrafterLockedUpdatePacket.STREAM_CODEC,
            wrapHandler(TieredAutocrafterLockedUpdatePacket::handle)
        );
        registrar.playToClient(
            TieredAutocrafterNameUpdatePacket.PACKET_TYPE,
            TieredAutocrafterNameUpdatePacket.STREAM_CODEC,
            wrapHandler(TieredAutocrafterNameUpdatePacket::handle)
        );
        registrar.playToClient(
            SetSidedResourcesOnPatternGridMenuPacket.PACKET_TYPE,
            SetSidedResourcesOnPatternGridMenuPacket.STREAM_CODEC,
            wrapHandler(SetSidedResourcesOnPatternGridMenuPacket::handle)
        );
        registrar.playToClient(
            ReplaceSidedResourceOnPatternGridMenuPacket.PACKET_TYPE,
            ReplaceSidedResourceOnPatternGridMenuPacket.STREAM_CODEC,
            wrapHandler(ReplaceSidedResourceOnPatternGridMenuPacket::handle)
        );
        registrar.playToClient(
            ClearSidedResourceOnPatternGridMenuPacket.PACKET_TYPE,
            ClearSidedResourceOnPatternGridMenuPacket.STREAM_CODEC,
            wrapHandler(ClearSidedResourceOnPatternGridMenuPacket::handle)
        );
    }

    private static void registerClientToServerPackets(final PayloadRegistrar registrar) {
        registrar.playToServer(
            ChangeAdvancedResourceSlotPacket.PACKET_TYPE,
            ChangeAdvancedResourceSlotPacket.STREAM_CODEC,
            wrapHandler(ChangeAdvancedResourceSlotPacket::handle)
        );
        registrar.playToServer(
            SetAdvancedFilterPacket.PACKET_TYPE,
            SetAdvancedFilterPacket.STREAM_CODEC,
            wrapHandler(SetAdvancedFilterPacket::handle)
        );
        registrar.playToServer(
            TieredAutocrafterNameChangePacket.PACKET_TYPE,
            TieredAutocrafterNameChangePacket.STREAM_CODEC,
            wrapHandler(TieredAutocrafterNameChangePacket::handle)
        );
        registrar.playToServer(
            RequestSidedResourcesPacket.PACKET_TYPE,
            RequestSidedResourcesPacket.STREAM_CODEC,
            wrapHandler(RequestSidedResourcesPacket::handle)
        );
        registrar.playToServer(
            SetSidedResourcesOnPatternGridBlockPacket.PACKET_TYPE,
            SetSidedResourcesOnPatternGridBlockPacket.STREAM_CODEC,
            wrapHandler(SetSidedResourcesOnPatternGridBlockPacket::handle)
        );
    }

    private static <T extends CustomPacketPayload> IPayloadHandler<T> wrapHandler(final PacketHandler<T> handler) {
        return (packet, ctx) -> handler.handle(packet, ctx::player);
    }

    private void registerCreativeModeTabListener(final BuildCreativeModeTabContentsEvent e) {
        final ResourceKey<CreativeModeTab> creativeModeTab = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            RefinedStorageApi.INSTANCE.getCreativeModeTabId()
        );
        final ResourceKey<CreativeModeTab> coloredCreativeModeTab = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            RefinedStorageApi.INSTANCE.getColoredCreativeModeTabId()
        );

        if (e.getTabKey().equals(creativeModeTab)) {
            CreativeModeTabItems.appendBlocks(e::accept);
        } else if (e.getTabKey().equals(coloredCreativeModeTab)) {
            CreativeModeTabItems.appendColoredVariants(e::accept);
        }
    }

    private void onTagsUpdated(final TagsUpdatedEvent event) {
        TagsCache.invalidateAll();
    }

    private record ForgeRegistryCallback<T>(DeferredRegister<T> registry) implements RegistryCallback<T> {
        @Override
        public <R extends T> Supplier<R> register(final ResourceLocation id, final Supplier<R> value) {
            return registry.register(id.getPath(), value);
        }
    }
}
