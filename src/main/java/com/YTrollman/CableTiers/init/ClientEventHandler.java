package com.YTrollman.CableTiers.init;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.gui.*;
import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry;
import com.refinedmods.refinedstorage.render.model.BakedModelCableCover;
import com.refinedmods.refinedstorage.render.model.FullbrightBakedModel;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.ResourceLocation;
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
                RenderTypeLookup.setRenderLayer(type.getBlock(tier), RenderType.cutout());
            }

            ScreenManager.register(ContentType.EXPORTER.getContainerType(tier), TieredExporterScreen::new);
            ScreenManager.register(ContentType.IMPORTER.getContainerType(tier), TieredImporterScreen::new);
            ScreenManager.register(ContentType.CONSTRUCTOR.getContainerType(tier), TieredConstructorScreen::new);
            ScreenManager.register(ContentType.DESTRUCTOR.getContainerType(tier), TieredDestructorScreen::new);
            ScreenManager.register(ContentType.DISK_MANIPULATOR.getContainerType(tier), TieredDiskManipulatorScreen::new);
            ScreenManager.register(ContentType.REQUESTER.getContainerType(tier), TieredRequesterScreen::new);

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
