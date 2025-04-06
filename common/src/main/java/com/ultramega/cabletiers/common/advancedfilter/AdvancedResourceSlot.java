package com.ultramega.cabletiers.common.advancedfilter;

import com.ultramega.cabletiers.common.packet.OpenAdvancedFilterPacket;
import com.ultramega.cabletiers.common.support.AbstractTieredFilterContainerMenu;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlotType;

import java.util.Optional;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AdvancedResourceSlot extends ResourceSlot {
    private final AbstractTieredFilterContainerMenu<?> menu;
    private final Player player;

    public AdvancedResourceSlot(final AbstractTieredFilterContainerMenu<?> menu,
                                final Player player,
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
        if (stack.isEmpty() && menu instanceof AbstractTieredFilterContainerMenu<?> containerMenu && !Screen.hasShiftDown()) {
            if (player instanceof ServerPlayer serverPlayer) {
                final PlatformResourceKey filterResource = resourceContainer.getResource(getContainerSlot());

                Platform.INSTANCE.sendPacketToClient(serverPlayer, new OpenAdvancedFilterPacket(
                    index,
                    Optional.ofNullable(containerMenu.getTagFilter(index)),
                    Optional.ofNullable(filterResource)));
            }
        } else {
            super.change(stack, tryAlternatives);
        }
    }
}
