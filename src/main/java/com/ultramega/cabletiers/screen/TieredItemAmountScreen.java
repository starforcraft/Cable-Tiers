package com.ultramega.cabletiers.screen;

import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.ItemAmountScreen;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.network.TieredSetFilterSlotMessage;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class TieredItemAmountScreen extends ItemAmountScreen {
    private final int containerSlot;
    private final ItemStack stack;
    private final CableTier tier;

    public TieredItemAmountScreen(CableTier tier, BaseScreen parent, Player player, int containerSlot, ItemStack stack, int maxAmount, @Nullable UnaryOperator<Screen> alternativesScreenFactory) {
        super(parent, player, containerSlot, stack, maxAmount, alternativesScreenFactory);
        this.tier          = tier;
        this.containerSlot = containerSlot;
        this.stack         = stack;
    }

    @Override
    protected void onOkButtonPressed(boolean shiftDown) {
        try {
            int amount = Integer.parseInt(amountField.getValue());

            CableTiers.NETWORK_HANDLER.sendToServer(new TieredSetFilterSlotMessage(tier.getSlotsMultiplier(), containerSlot, stack.getItem(), amount));

            close();
        } catch (NumberFormatException e) {
            // NO OP
        }
    }
}