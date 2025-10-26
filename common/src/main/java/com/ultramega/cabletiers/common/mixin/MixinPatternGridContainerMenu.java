package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedResourceAmount;
import com.ultramega.cabletiers.common.packet.c2s.SetSidedResourcesOnPatternGridBlockPacket;
import com.ultramega.cabletiers.common.packet.s2c.ReplaceSidedResourceOnPatternGridMenuPacket;
import com.ultramega.cabletiers.common.utils.PlayerInventoryGetter;
import com.ultramega.cabletiers.common.utils.SidedInput;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu;
import com.refinedmods.refinedstorage.common.grid.AbstractGridContainerMenu;
import com.refinedmods.refinedstorage.common.grid.GridData;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.ultramega.cabletiers.common.utils.SidedInputUtil.isProcessingInputSlot;

@Mixin(PatternGridContainerMenu.class)
public abstract class MixinPatternGridContainerMenu extends AbstractGridContainerMenu implements SidedInput, PlayerInventoryGetter {
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
        this.cabletiers$clearSidedResources();
    }

    @Inject(method = "transferProcessingRecipe", at = @At("HEAD"), remap = false)
    private void transferProcessingRecipe(final CallbackInfo ci) {
        this.cabletiers$clearSidedResources();
    }

    @Unique
    private void cabletiers$clearSidedResources() {
        this.cabletiers$sidedResources.replaceAll(ignored -> Optional.empty());
        Platform.INSTANCE.sendPacketToServer(new SetSidedResourcesOnPatternGridBlockPacket(cabletiers$sidedResources));
    }

    @Unique
    @Override
    public void cabletiers$setSidedResources(final List<Optional<SidedResourceAmount>> sidedResources) {
        this.cabletiers$sidedResources = sidedResources;
    }

    @Unique
    @Override
    public void cabletiers$replaceSidedResource(@Nullable final ResourceAmount resource, final int index) {
        this.cabletiers$sidedResources.get(index).ifPresent(ignored -> this.cabletiers$sidedResources.set(index,
            resource == null
                ? Optional.empty() 
                : Optional.of(new SidedResourceAmount(resource, Optional.empty()))));

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

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        // Find correct index and remove sided resource
        int index = 0;
        for (final ResourceSlot slot : getResourceSlots()) {
            if (isProcessingInputSlot(slot)) {
                if (slot.index == slotIndex) {
                    final ResourceAmount resourceAmount = slot.getResource() != null ? new ResourceAmount(slot.getResource(), slot.getAmount()) : null;
                    Platform.INSTANCE.sendPacketToClient(serverPlayer, new ReplaceSidedResourceOnPatternGridMenuPacket(Optional.ofNullable(resourceAmount), index));
                    break;
                }
                index++;
            }
        }
    }

    @Override
    public Inventory cabletiers$getPlayerInventory() {
        return playerInventory;
    }
}
