package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.widget.AbstractYesNoSideButtonWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;

class AutocrafterImportSideButtonWidget extends AbstractYesNoSideButtonWidget {
    private static final MutableComponent TITLE = createCableTiersTranslation("gui", "act_as_importer");
    private static final Component HELP = createCableTiersTranslation("gui", "act_as_importer.help");
    private static final ResourceLocation YES_SPRITE = createCableTiersIdentifier("widget/side_button/act_as_importer_yes");
    private static final ResourceLocation NO_SPRITE = createCableTiersIdentifier("widget/side_button/act_as_importer_no");

    AutocrafterImportSideButtonWidget(final ClientProperty<Boolean> property) {
        super(property, TITLE, YES_SPRITE, NO_SPRITE);
    }

    @Override
    protected Component getHelpText() {
        return HELP;
    }
}
