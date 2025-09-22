package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputPatternState;
import com.ultramega.cabletiers.common.utils.SidedInput;

import com.refinedmods.refinedstorage.common.autocrafting.PatternItem;
import com.refinedmods.refinedstorage.common.autocrafting.PatternState;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternType;
import com.refinedmods.refinedstorage.common.content.DataComponents;
import com.refinedmods.refinedstorage.common.util.ClientPlatformUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatternItem.class)
public class MixinPatternItem {
    @Unique
    private static final Map<UUID, Boolean> SENT_SIDED_RESOURCES_DATA = new HashMap<>();

    @Inject(method = "getTooltipImage", at = @At("HEAD"))
    public void getTooltipImage(final ItemStack stack, final CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        final PatternState state = stack.get(DataComponents.INSTANCE.getPatternState());
        if (state == null || SENT_SIDED_RESOURCES_DATA.computeIfAbsent(state.id(), (uuid) -> false)) {
            return;
        }

        final Level level = ClientPlatformUtil.getClientLevel();
        if (level == null) {
            return;
        }

        final SidedInputPatternState sidedInputState = stack.get(com.ultramega.cabletiers.common.registry.DataComponents.INSTANCE.getSidedInputPatternState());
        if (sidedInputState == null) {
            return;
        }

        if (state.type() == PatternType.PROCESSING) {
            if (MixinPatternTooltipCacheInvoker.getCache().get(state.id()) instanceof SidedInput sidedInput) {
                sidedInput.cabletiers$setSidedResources(sidedInputState.sidedResources());
                SENT_SIDED_RESOURCES_DATA.put(state.id(), true);
            }
        }
    }
}
