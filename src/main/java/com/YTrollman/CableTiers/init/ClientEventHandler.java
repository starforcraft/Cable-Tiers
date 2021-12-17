package com.YTrollman.CableTiers.init;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.gui.*;
import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry;
import com.refinedmods.refinedstorage.render.model.BakedModelCableCover;
import com.refinedmods.refinedstorage.render.model.FullbrightBakedModel;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEventHandler {

    private static final BakedModelOverrideRegistry bakedModelOverrideRegistry = new BakedModelOverrideRegistry();

    public ClientEventHandler() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
    }

    public void init(FMLClientSetupEvent event) {
        for (CableTier tier : CableTier.VALUES) {
            for (ContentType<?, ?, ?, ?> type : ContentType.CONTENT_TYPES) {
                ItemBlockRenderTypes.setRenderLayer(type.getBlock(tier), RenderType.cutout());
            }

            MenuScreens.register(ContentType.EXPORTER.getContainerType(tier), TieredExporterScreen::new);
            MenuScreens.register(ContentType.IMPORTER.getContainerType(tier), TieredImporterScreen::new);
            MenuScreens.register(ContentType.CONSTRUCTOR.getContainerType(tier), TieredConstructorScreen::new);
            MenuScreens.register(ContentType.DESTRUCTOR.getContainerType(tier), TieredDestructorScreen::new);
            MenuScreens.register(ContentType.DISK_MANIPULATOR.getContainerType(tier), TieredDiskManipulatorScreen::new);
            MenuScreens.register(ContentType.REQUESTER.getContainerType(tier), TieredRequesterScreen::new);

            bakedModelOverrideRegistry.add(new ResourceLocation("cabletiers", ContentType.EXPORTER.getName(tier)), (base, registry) -> new BakedModelCableCover(base));
            bakedModelOverrideRegistry.add(new ResourceLocation("cabletiers", ContentType.IMPORTER.getName(tier)), (base, registry) -> new BakedModelCableCover(base));
            bakedModelOverrideRegistry.add(new ResourceLocation("cabletiers", ContentType.CONSTRUCTOR.getName(tier)), (base, registry) -> new BakedModelCableCover(base));
            bakedModelOverrideRegistry.add(new ResourceLocation("cabletiers", ContentType.DESTRUCTOR.getName(tier)), (base, registry) -> new BakedModelCableCover(base));
        }
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        FullbrightBakedModel.invalidateCache();

        for (ResourceLocation id : e.getModelRegistry().keySet()) {
            BakedModelOverrideRegistry.BakedModelOverrideFactory factory = bakedModelOverrideRegistry.get(new ResourceLocation(id.getNamespace(), id.getPath()));

            if (factory != null) {
                e.getModelRegistry().put(id, factory.create(e.getModelRegistry().get(id), e.getModelRegistry()));
            }
        }
    }
}
