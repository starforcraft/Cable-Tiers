package com.YTrollman.CableTiers.init;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.gui.*;
import com.YTrollman.CableTiers.registry.ModBlocks;
import com.YTrollman.CableTiers.registry.ModContainers;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CableTiers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        for (CableTier tier : CableTier.VALUES) {
            for (ContentType<?, ?, ?, ?> type : ContentType.CONTENT_TYPES) {
                RenderTypeLookup.setRenderLayer(type.getBlock(tier), RenderType.cutout());
            }

            ScreenManager.register(ContentType.IMPORTER.getContainerType(tier), TieredImporterScreen::new);
            ScreenManager.register(ContentType.DESTRUCTOR.getContainerType(tier), TieredDestructorScreen::new);
        }

        ScreenManager.register(ModContainers.ELITE_CONSTRUCTOR_CONTAINER.get(), EliteConstructorScreen::new);
        ScreenManager.register(ModContainers.ELITE_EXPORTER_CONTAINER.get(), EliteExporterScreen::new);
        ScreenManager.register(ModContainers.ULTRA_CONSTRUCTOR_CONTAINER.get(), UltraConstructorScreen::new);
        ScreenManager.register(ModContainers.ULTRA_EXPORTER_CONTAINER.get(), UltraExporterScreen::new);
        ScreenManager.register(ModContainers.CREATIVE_CONSTRUCTOR_CONTAINER.get(), CreativeConstructorScreen::new);
        ScreenManager.register(ModContainers.CREATIVE_EXPORTER_CONTAINER.get(), CreativeExporterScreen::new);

        RenderTypeLookup.setRenderLayer(ModBlocks.ELITE_CONSTRUCTOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ELITE_EXPORTER.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ULTRA_CONSTRUCTOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ULTRA_EXPORTER.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.CREATIVE_CONSTRUCTOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.CREATIVE_EXPORTER.get(), RenderType.cutout());
    }
}