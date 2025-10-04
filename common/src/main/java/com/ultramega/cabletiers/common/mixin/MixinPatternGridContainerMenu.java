package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedResourceAmount;
import com.ultramega.cabletiers.common.packet.c2s.SetSidedResourcesOnPatternGridBlockPacket;
import com.ultramega.cabletiers.common.packet.s2c.RemoveSidedResourcesOnPatternGridMenuPacket;
import com.ultramega.cabletiers.common.utils.SidedInput;
import com.ultramega.cabletiers.common.utils.ValidSlot;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu;
import com.refinedmods.refinedstorage.common.grid.AbstractGridContainerMenu;
import com.refinedmods.refinedstorage.common.grid.GridData;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputScreen.isProcessingInputSlot;

@Mixin(PatternGridContainerMenu.class)
public abstract class MixinPatternGridContainerMenu extends AbstractGridContainerMenu implements SidedInput {
    @Shadow
    @Nullable
    private Slot patternOutputSlot;
    @Shadow
    @Final
    private Container patternOutput;

    @Unique
    private List<Optional<SidedResourceAmount>> cabletiers$sidedResources = new ArrayList<>();

    protected MixinPatternGridContainerMenu(final MenuType<? extends AbstractGridContainerMenu> menuType,
                                            final int syncId,
                                            final Inventory playerInventory,
                                            final GridData gridData) {
        super(menuType, syncId, playerInventory, gridData);
    }

    @Inject(method = "sendClear", at = @At("HEAD"), remap = false)
    private void sendClear(final CallbackInfo ci) {
        this.cabletiers$sidedResources.replaceAll(ignored -> Optional.empty());

        Platform.INSTANCE.sendPacketToServer(new SetSidedResourcesOnPatternGridBlockPacket(cabletiers$sidedResources));
    }

    @Inject(method = "addPatternSlots", at = @At("TAIL"), remap = false)
    private void addPatternSlots(final int playerInventoryY, final CallbackInfo ci) {
        if (patternOutputSlot instanceof ValidSlot validSlot) {
            validSlot.cabletiers$setPatternGridContainerMenu(patternOutput);
        }
    }

    @Unique
    @Override
    public void cabletiers$setSidedResources(final List<Optional<SidedResourceAmount>> sidedResources) {
        this.cabletiers$sidedResources = sidedResources;
    }

    @Unique
    @Override
    public void cabletiers$removeSidedResources(final int index) {
        this.cabletiers$sidedResources.set(index, Optional.empty());

        Platform.INSTANCE.sendPacketToServer(new SetSidedResourcesOnPatternGridBlockPacket(cabletiers$sidedResources));
    }

    @Unique
    @Override
    public List<Optional<SidedResourceAmount>> cabletiers$getSidedResources() {
        return cabletiers$sidedResources;
    }

    @Unique
    @Override
    public void handleResourceSlotChange(final int slotIndex, final boolean tryAlternatives) {
        super.handleResourceSlotChange(slotIndex, tryAlternatives);

        if (!getCarried().isEmpty() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        // Find correct index and remove sided resource
        int index = 0;
        for (final ResourceSlot slot : getResourceSlots()) {
            if (isProcessingInputSlot(slot)) {
                if (slot.index == slotIndex) {
                    Platform.INSTANCE.sendPacketToClient(serverPlayer, new RemoveSidedResourcesOnPatternGridMenuPacket(index));
                    break;
                }
                index++;
            }
        }
    }
}
