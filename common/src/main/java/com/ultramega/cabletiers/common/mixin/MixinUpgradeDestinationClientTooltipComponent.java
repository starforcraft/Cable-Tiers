package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeMapping;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinationClientTooltipComponent;

import java.util.Set;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = UpgradeDestinationClientTooltipComponent.class, remap = false)
public abstract class MixinUpgradeDestinationClientTooltipComponent {
    @Shadow
    @Final
    private Set<UpgradeMapping> mappings;

    @Shadow
    protected abstract void renderMapping(Font font, int x, int y, GuiGraphics graphics, UpgradeMapping mapping);

    /**
     * @author Ultramega
     * @reason Hide our upgrade destinations
     */
    @Overwrite
    public void renderImage(final Font font, final int x, final int y, final GuiGraphics graphics) {
        int yy = y;
        for (final UpgradeMapping mapping : mappings) {
            if (mapping.destination().getStackRepresentation().isEmpty() || mapping.destination().getName().getString().isEmpty()) {
                continue;
            }

            renderMapping(font, x, yy, graphics, mapping);
            yy += 18;
        }
    }

    /**
     * @author Ultramega
     * @reason Fix height after hiding the upgrade destinations
     */
    @Overwrite
    public int getHeight() {
        return 18 * (int) mappings.stream()
            .map(UpgradeMapping::destination)
            .filter(dest -> !dest.getStackRepresentation().isEmpty() && !dest.getName().getString().isEmpty())
            .count();
    }
}
