package com.YTrollman.CableTiers.init;

import com.YTrollman.CableTiers.gui.CreativeConstructorScreen;
import com.YTrollman.CableTiers.gui.CreativeDestructorScreen;
import com.YTrollman.CableTiers.gui.CreativeExporterScreen;
import com.YTrollman.CableTiers.gui.CreativeImporterScreen;
import com.YTrollman.CableTiers.gui.EliteConstructorScreen;
import com.YTrollman.CableTiers.gui.EliteDestructorScreen;
import com.YTrollman.CableTiers.gui.EliteExporterScreen;
import com.YTrollman.CableTiers.gui.EliteImporterScreen;
import com.YTrollman.CableTiers.gui.UltraConstructorScreen;
import com.YTrollman.CableTiers.gui.UltraDestructorScreen;
import com.YTrollman.CableTiers.gui.UltraExporterScreen;
import com.YTrollman.CableTiers.gui.UltraImporterScreen;
import com.YTrollman.CableTiers.registry.ModBlocks;
import com.YTrollman.CableTiers.registry.ModContainers;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEventHandler {

    public static void init(final FMLClientSetupEvent event) 
    {
        ScreenManager.register(ModContainers.ELITE_CONSTRUCTOR_CONTAINER.get(), EliteConstructorScreen::new);
        ScreenManager.register(ModContainers.ELITE_DESTRUCTOR_CONTAINER.get(), EliteDestructorScreen::new);
        ScreenManager.register(ModContainers.ELITE_IMPORTER_CONTAINER.get(), EliteImporterScreen::new);
        ScreenManager.register(ModContainers.ELITE_EXPORTER_CONTAINER.get(), EliteExporterScreen::new);
        ScreenManager.register(ModContainers.ULTRA_CONSTRUCTOR_CONTAINER.get(), UltraConstructorScreen::new);
        ScreenManager.register(ModContainers.ULTRA_DESTRUCTOR_CONTAINER.get(), UltraDestructorScreen::new);
        ScreenManager.register(ModContainers.ULTRA_IMPORTER_CONTAINER.get(), UltraImporterScreen::new);
        ScreenManager.register(ModContainers.ULTRA_EXPORTER_CONTAINER.get(), UltraExporterScreen::new);
        ScreenManager.register(ModContainers.CREATIVE_CONSTRUCTOR_CONTAINER.get(), CreativeConstructorScreen::new);
        ScreenManager.register(ModContainers.CREATIVE_DESTRUCTOR_CONTAINER.get(), CreativeDestructorScreen::new);
        ScreenManager.register(ModContainers.CREATIVE_IMPORTER_CONTAINER.get(), CreativeImporterScreen::new);
        ScreenManager.register(ModContainers.CREATIVE_EXPORTER_CONTAINER.get(), CreativeExporterScreen::new);
        
        RenderTypeLookup.setRenderLayer(ModBlocks.ELITE_DESTRUCTOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ELITE_CONSTRUCTOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ELITE_IMPORTER.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ELITE_EXPORTER.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ULTRA_DESTRUCTOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ULTRA_CONSTRUCTOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ULTRA_IMPORTER.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ULTRA_EXPORTER.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.CREATIVE_DESTRUCTOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.CREATIVE_CONSTRUCTOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.CREATIVE_IMPORTER.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.CREATIVE_EXPORTER.get(), RenderType.cutout());
    }
}