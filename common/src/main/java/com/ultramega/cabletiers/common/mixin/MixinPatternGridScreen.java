package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.autocrafting.sidedinput.ExtendedCustomButton;
import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputScreen;
import com.ultramega.cabletiers.common.packet.c2s.RequestSidedResourcesPacket;
import com.ultramega.cabletiers.common.utils.SidedInput;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridScreen;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternType;
import com.refinedmods.refinedstorage.common.grid.screen.AbstractGridScreen;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;

@Mixin(PatternGridScreen.class)
public abstract class MixinPatternGridScreen extends AbstractGridScreen<PatternGridContainerMenu> implements SidedInput {
    @Unique
    private static final MutableComponent SIDED_INPUT = createCableTiersTranslation("gui", "pattern_grid.sided_input");
    @Unique
    private static final MutableComponent SIDED_INPUT_HELP = createCableTiersTranslation("gui", "pattern_grid.sided_input.help");

    @Unique
    private static final WidgetSprites SIDED_INPUT_BUTTON_SPRITES = new WidgetSprites(
        createCableTiersIdentifier("widget/sided_input"),
        createCableTiersIdentifier("widget/sided_input_disabled"),
        createCableTiersIdentifier("widget/sided_input_focused"),
        createCableTiersIdentifier("widget/sided_input_disabled")
    );

    @Unique
    @Nullable
    private Button cabletiers$createSidedInputButton;

    protected MixinPatternGridScreen(final PatternGridContainerMenu menu,
                                     final Inventory playerInventory,
                                     final Component title,
                                     final int bottomHeight) {
        super(menu, playerInventory, title, bottomHeight);
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void init(final CallbackInfo ci) {
        this.cabletiers$createSidedInputButton = new ExtendedCustomButton(
            leftPos + 124,
            (topPos + imageHeight - bottomHeight + 5 + 4 + 9) + (int) (CLEAR_BUTTON_SIZE * 1.5),
            CLEAR_BUTTON_SIZE,
            CLEAR_BUTTON_SIZE,
            SIDED_INPUT_BUTTON_SPRITES,
            b -> {
                if (getMenu() instanceof SidedInput sidedInput) {
                    Minecraft.getInstance().setScreen(new SidedInputScreen(getMenu(), title, sidedInput.cabletiers$getSidedResources()));
                }
            },
            SIDED_INPUT,
            SIDED_INPUT_HELP
        );
        addRenderableWidget(cabletiers$createSidedInputButton);

        Platform.INSTANCE.sendPacketToServer(new RequestSidedResourcesPacket());
    }

    @Inject(method = "patternTypeChanged", at = @At("TAIL"), remap = false)
    public void patternTypeChanged(final PatternType newPatternType, final CallbackInfo ci) {
        if (cabletiers$createSidedInputButton != null) {
            cabletiers$createSidedInputButton.visible = newPatternType == PatternType.PROCESSING;
        }
    }
}
