package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeMapping;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinationClientTooltipComponent;

import java.util.Set;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = UpgradeDestinationClientTooltipComponent.class)
public abstract class UpgradeDestinationClientTooltipComponentMixin {
    @Shadow(remap = false)
    @Final
    private Set<UpgradeMapping> mappings;

    @Shadow(remap = false)
    protected abstract void renderMapping(Font font, int x, int y, GuiGraphicsExtractor graphics, UpgradeMapping mapping);

    /**
     * @author Ultramega
     * @reason Hide our upgrade destinations
     */
    @Overwrite
    public void extractImage(final Font font, final int x, final int y, final int w, final int h, final GuiGraphicsExtractor graphics) {
        int yy = y;
        for (final UpgradeMapping mapping : this.mappings) {
            if (mapping.destination().getStackRepresentation().isEmpty() || mapping.destination().getName().getString().isEmpty()) {
                continue;
            }

            this.renderMapping(font, x, yy, graphics, mapping);
            yy += 18;
        }
    }

    /**
     * @author Ultramega
     * @reason Fix height after hiding the upgrade destinations
     */
    @Overwrite
    public int getHeight(final Font fon) {
        return 18 * (int) this.mappings.stream()
            .map(UpgradeMapping::destination)
            .filter(dest -> !dest.getStackRepresentation().isEmpty() && !dest.getName().getString().isEmpty())
            .count();
    }
}
