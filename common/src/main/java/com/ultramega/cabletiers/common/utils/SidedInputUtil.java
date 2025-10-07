package com.ultramega.cabletiers.common.utils;

import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;

import net.minecraft.network.chat.MutableComponent;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public final class SidedInputUtil {
    private static final MutableComponent INPUT_HELP = createTranslation(
        "gui",
        "pattern_grid.processing.input_slots_help"
    );

    private SidedInputUtil() {
    }

    public static boolean isProcessingInputSlot(final ResourceSlot slot) {
        // Unfortunately, this ugly workaround has to be used because ProcessingMatrixInputResourceContainer is private
        return slot.getHelpText().equals(INPUT_HELP);
    }
}
