package com.ultramega.cabletiers.common.constructordestructor;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.support.AbstractAdvancedFilterScreen;

import com.refinedmods.refinedstorage.common.storage.FilterModeSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.AbstractYesNoSideButtonWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class TieredDestructorScreen extends AbstractAdvancedFilterScreen<TieredDestructorContainerMenu> {
    public TieredDestructorScreen(final TieredDestructorContainerMenu menu,
                                  final Inventory playerInventory,
                                  final Component title,
                                  final CableTiers tier) {
        super(menu, playerInventory, title, tier);
    }

    @Override
    protected void init() {
        super.init();
        addSideButton(new FilterModeSideButtonWidget(
            getMenu().getProperty(PropertyTypes.FILTER_MODE),
            createTranslation("gui", "destructor.filter_mode.allow.help"),
            createTranslation("gui", "destructor.filter_mode.block.help")
        ));
        addSideButton(new DestructorPickupItemsSideButtonWidget(
            getMenu().getProperty(ConstructorDestructorPropertyTypes.PICKUP_ITEMS)
        ));
    }

    /**
     * Exact copy of {@link com.refinedmods.refinedstorage.common.constructordestructor.DestructorPickupItemsSideButtonWidget}
     */
    static class DestructorPickupItemsSideButtonWidget extends AbstractYesNoSideButtonWidget {
        private static final MutableComponent TITLE = createTranslation("gui", "destructor.pickup_items");
        private static final ResourceLocation YES = createIdentifier("widget/side_button/destructor_pickup_items/yes");
        private static final ResourceLocation NO = createIdentifier("widget/side_button/destructor_pickup_items/no");

        DestructorPickupItemsSideButtonWidget(final ClientProperty<Boolean> property) {
            super(property, TITLE, YES, NO);
        }
    }
}
