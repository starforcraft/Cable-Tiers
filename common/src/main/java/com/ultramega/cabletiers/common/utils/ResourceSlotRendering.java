package com.ultramega.cabletiers.common.utils;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.RefinedStorageClientApi;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceRendering;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import static com.refinedmods.refinedstorage.common.support.ResourceSlotRendering.renderAmount;

public class ResourceSlotRendering {
    private ResourceSlotRendering() {
    }

    public static List<Component> render(final GuiGraphics graphics,
                                         final ResourceSlot slot,
                                         final int x,
                                         final int y) {
        final ResourceKey resource = slot.getResource();
        if (resource == null) {
            return List.of();
        }

        final ResourceRendering rendering = RefinedStorageClientApi.INSTANCE.getResourceRendering(resource.getClass());
        rendering.render(resource, graphics, x, y);
        if (slot.shouldRenderAmount()) {
            renderAmount(graphics, x, y, slot.getAmount(), rendering);
        }

        return rendering.getTooltip(resource);
    }
}
