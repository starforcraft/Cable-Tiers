package com.YTrollman.CableTiers.init;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.gui.*;
import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry;
import com.refinedmods.refinedstorage.render.model.baked.CableCoverBakedModel;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEventHandler {
    private static final BakedModelOverrideRegistry BAKED_MODEL_OVERRIDE_REGISTRY = new BakedModelOverrideRegistry();

    public ClientEventHandler() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
    }

    public void init(FMLClientSetupEvent event) {
        for (CableTier tier : CableTier.VALUES) {
            MenuScreens.register(ContentType.EXPORTER.getContainerType(tier), TieredExporterScreen::new);
            MenuScreens.register(ContentType.IMPORTER.getContainerType(tier), TieredImporterScreen::new);
            MenuScreens.register(ContentType.CONSTRUCTOR.getContainerType(tier), TieredConstructorScreen::new);
            MenuScreens.register(ContentType.DESTRUCTOR.getContainerType(tier), TieredDestructorScreen::new);
            MenuScreens.register(ContentType.DISK_MANIPULATOR.getContainerType(tier), TieredDiskManipulatorScreen::new);
            MenuScreens.register(ContentType.REQUESTER.getContainerType(tier), TieredRequesterScreen::new);

            BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(CableTiers.MOD_ID, ContentType.EXPORTER.getName(tier)), (base, registry) -> new CableCoverBakedModel(base));
            BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(CableTiers.MOD_ID, ContentType.IMPORTER.getName(tier)), (base, registry) -> new CableCoverBakedModel(base));
            BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(CableTiers.MOD_ID, ContentType.CONSTRUCTOR.getName(tier)), (base, registry) -> new CableCoverBakedModel(base));
            BAKED_MODEL_OVERRIDE_REGISTRY.add(new ResourceLocation(CableTiers.MOD_ID, ContentType.DESTRUCTOR.getName(tier)), (base, registry) -> new CableCoverBakedModel(base));
        }
    }

    @SubscribeEvent
    public void onModelBake(ModelEvent.BakingCompleted e) {
        for (ResourceLocation id : e.getModels().keySet()) {
            BakedModelOverrideRegistry.BakedModelOverrideFactory factory = BAKED_MODEL_OVERRIDE_REGISTRY.get(new ResourceLocation(id.getNamespace(), id.getPath()));

            if (factory != null) {
                e.getModels().put(id, factory.create(e.getModels().get(id), e.getModels()));
            }
        }
    }
}
