package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.widget.AbstractSideButtonWidget;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;

public class AutocrafterImportModeSideButtonWidget extends AbstractSideButtonWidget {
    private static final String PREFIX = "autocrafter.import_mode";

    private static final MutableComponent TITLE = createCableTiersTranslation("gui", PREFIX);
    private static final ResourceLocation DONT_IMPORT = createCableTiersIdentifier("widget/side_button/dont_import");
    private static final ResourceLocation IMPORT_EVERYTHING = createCableTiersIdentifier("widget/side_button/import_everything");
    private static final ResourceLocation IMPORT_PATTERN_OUTPUTS = createCableTiersIdentifier("widget/side_button/import_pattern_outputs");
    private static final ResourceLocation IMPORT_REQUESTED_RESOURCES = createCableTiersIdentifier("widget/side_button/import_requested_resources");

    private static final List<MutableComponent> DONT_IMPORT_TITLE = List.of(createCableTiersTranslation(
        "gui", PREFIX + ".dont_import"
    ).withStyle(ChatFormatting.GRAY));
    private static final List<MutableComponent> IMPORT_EVERYTHING_TITLE = List.of(createCableTiersTranslation(
        "gui", PREFIX + ".import_everything"
    ).withStyle(ChatFormatting.GRAY));
    private static final List<MutableComponent> IMPORT_PATTERN_OUTPUTS_TITLE = List.of(createCableTiersTranslation(
        "gui", PREFIX + ".import_pattern_outputs"
    ).withStyle(ChatFormatting.GRAY));
    private static final List<MutableComponent> IMPORT_REQUESTED_RESOURCES_TITLE = List.of(createCableTiersTranslation(
        "gui", PREFIX + ".import_requested_resources"
    ).withStyle(ChatFormatting.GRAY));

    private final ClientProperty<ImportMode> property;

    AutocrafterImportModeSideButtonWidget(final ClientProperty<ImportMode> property) {
        super(createPressAction(property));
        this.property = property;
    }

    private static OnPress createPressAction(final ClientProperty<ImportMode> property) {
        return btn -> property.setValue(property.getValue().toggle());
    }

    @Override
    protected ResourceLocation getSprite() {
        return switch (property.getValue()) {
            case DONT_IMPORT -> DONT_IMPORT;
            case IMPORT_EVERYTHING -> IMPORT_EVERYTHING;
            case IMPORT_PATTERN_OUTPUTS -> IMPORT_PATTERN_OUTPUTS;
            case IMPORT_REQUESTED_RESOURCES -> IMPORT_REQUESTED_RESOURCES;
        };
    }

    @Override
    protected MutableComponent getTitle() {
        return TITLE;
    }

    @Override
    protected List<MutableComponent> getSubText() {
        return switch (property.getValue()) {
            case DONT_IMPORT -> DONT_IMPORT_TITLE;
            case IMPORT_EVERYTHING -> IMPORT_EVERYTHING_TITLE;
            case IMPORT_PATTERN_OUTPUTS -> IMPORT_PATTERN_OUTPUTS_TITLE;
            case IMPORT_REQUESTED_RESOURCES -> IMPORT_REQUESTED_RESOURCES_TITLE;
        };
    }
}
