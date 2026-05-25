package com.ultramega.cabletiers.common.utils;

import com.refinedmods.refinedstorage.common.autocrafting.PatternSlot;

import net.minecraft.world.Container;
import net.minecraft.world.level.Level;

public class AdvancedPatternSlot extends PatternSlot {
    private final boolean isHighlightable;

    public AdvancedPatternSlot(final Container container, final int index, final int x, final int y, final Level level, final boolean isHighlightable) {
        super(container, index, x, y, level);
        this.isHighlightable = isHighlightable;
    }

    @Override
    public boolean isHighlightable() {
        return this.isHighlightable; // we render the highlight in the scissor render
    }
}
