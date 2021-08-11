package com.YTrollman.CableTiers.init;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.CableTiers;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.gui.*;
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

            ScreenManager.register(ContentType.EXPORTER.getContainerType(tier), TieredExporterScreen::new);
            ScreenManager.register(ContentType.IMPORTER.getContainerType(tier), TieredImporterScreen::new);
            ScreenManager.register(ContentType.CONSTRUCTOR.getContainerType(tier), TieredConstructorScreen::new);
            ScreenManager.register(ContentType.DESTRUCTOR.getContainerType(tier), TieredDestructorScreen::new);
            ScreenManager.register(ContentType.DISK_MANIPULATOR.getContainerType(tier), TieredDiskManipulatorScreen::new);
        }
    }
}
