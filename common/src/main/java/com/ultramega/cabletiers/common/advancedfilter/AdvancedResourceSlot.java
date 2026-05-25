package com.ultramega.cabletiers.common.advancedfilter;

import com.ultramega.cabletiers.common.packet.s2c.ShouldOpenAdvancedFilterPacket;
import com.ultramega.cabletiers.common.support.AbstractTieredFilterContainerMenu;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlotType;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class AdvancedResourceSlot extends ResourceSlot {
    private final AbstractTieredFilterContainerMenu<?> menu;
    @Nullable
    private final Player player;

    public AdvancedResourceSlot(final AbstractTieredFilterContainerMenu<?> menu,
                                @Nullable final Player player,
                                final ResourceContainer resourceContainer,
                                final int index,
                                final Component helpText,
                                final int x,
                                final int y,
                                final ResourceSlotType type) {
        super(resourceContainer, index, helpText, x, y, type);
        this.menu = menu;
        this.player = player;
    }

    @Override
    public void change(final ItemStack stack, final boolean tryAlternatives) {
        if (stack.isEmpty() && this.menu instanceof AbstractTieredFilterContainerMenu<?> containerMenu) {
            if (this.player instanceof ServerPlayer serverPlayer) {
                final PlatformResourceKey filterResource = this.resourceContainer.getResource(this.getContainerSlot());

                Platform.INSTANCE.sendPacketToClient(serverPlayer, new ShouldOpenAdvancedFilterPacket(
                    this.index,
                    Optional.ofNullable(containerMenu.getTagFilter(this.index)),
                    Optional.ofNullable(filterResource),
                    tryAlternatives));
            }
        } else {
            this.trulyChange(stack, tryAlternatives);
        }
    }

    public void trulyChange(final ItemStack stack, final boolean tryAlternatives) {
        super.change(stack, tryAlternatives);
    }
}
