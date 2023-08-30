package com.ultramega.cabletiers.mixin;

import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.AlternativesScreen;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.container.slot.TieredFilterSlot;
import com.ultramega.cabletiers.screen.TieredItemAmountScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseScreen.class)
public abstract class MixinBaseScreen extends AbstractContainerScreen {
    public MixinBaseScreen(AbstractContainerMenu containerMenu, Inventory inventory, Component component) {
        super(containerMenu, inventory, component);
    }

    @Inject(at = @At("HEAD"), method = "slotClicked", cancellable = true)
    public void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        boolean valid = type != ClickType.QUICK_MOVE && minecraft.player.containerMenu.getCarried().isEmpty();

        if (valid && slot instanceof TieredFilterSlot filterSlot && slot.isActive() && filterSlot.isSizeAllowed()) {
            if (!slot.getItem().isEmpty()) {
                minecraft.setScreen(new TieredItemAmountScreen(
                        filterSlot.getTier(),
                        (BaseScreen) (Object) this,
                        minecraft.player,
                        slot.index,
                        slot.getItem(),
                        getTieredStackInteractCount(filterSlot.getTier(), slot.getItem()),
                        null
                ));
                ci.cancel();
            }
        }
    }

    @Unique
    private int getTieredStackInteractCount(CableTier tier, ItemStack stack) {
        return (int) (stack.getMaxStackSize() * Math.pow(tier.getSlotsMultiplier(), 3));
    }
}
