package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.common.autocrafting.PatternTooltipCache;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PatternTooltipCache.class)
public interface InvokerPatternTooltipCache {
    @Accessor(value = "CACHE", remap = false)
    static Map<UUID, ClientTooltipComponent> getCache() {
        throw new AssertionError(); // Mixin will overwrite this
    }
}
