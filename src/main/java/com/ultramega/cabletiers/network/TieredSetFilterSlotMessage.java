package com.ultramega.cabletiers.network;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot;
import com.ultramega.cabletiers.container.slot.TieredFilterSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class TieredSetFilterSlotMessage {
    private final int slotsMultiplier;
    private final int containerSlot;
    private final ItemStack stack;

    public TieredSetFilterSlotMessage(int slotsMultiplier, int containerSlot, Item item, int itemAmount) {
        this.slotsMultiplier = slotsMultiplier;
        this.containerSlot = containerSlot;
        ItemStack stack = new ItemStack(item);
        stack.setCount(itemAmount);
        this.stack = stack;
    }

    public TieredSetFilterSlotMessage(int slotsMultiplier, int containerSlot, ItemStack stack, int itemAmount) {
        this.slotsMultiplier = slotsMultiplier;
        this.containerSlot = containerSlot;
        stack.setCount(itemAmount);
        this.stack = stack;
    }

    public static TieredSetFilterSlotMessage decode(FriendlyByteBuf buf) {
        return new TieredSetFilterSlotMessage(buf.readInt(), buf.readInt(), buf.readItem(), buf.readInt());
    }

    public static void encode(TieredSetFilterSlotMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.slotsMultiplier);
        buf.writeInt(message.containerSlot);
        buf.writeItem(message.stack);
        buf.writeInt(message.stack.getCount());
    }

    public static void handle(TieredSetFilterSlotMessage message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.stack.isEmpty() && message.stack.getCount() <= message.getTieredStackInteractCount(message.stack)) {
            Player player = ctx.get().getSender();

            if (player != null) {
                ctx.get().enqueueWork(() -> {
                    AbstractContainerMenu container = player.containerMenu;

                    if (container != null && message.containerSlot >= 0 && message.containerSlot < container.slots.size()) {
                        handle(message, container);
                    }
                });
            }
        }

        ctx.get().setPacketHandled(true);
    }

    private static void handle(TieredSetFilterSlotMessage message, AbstractContainerMenu container) {
        Slot slot = container.getSlot(message.containerSlot);

        if (slot instanceof TieredFilterSlot || slot instanceof LegacyFilterSlot) {
            Runnable postAction = () -> {
            };

            // Prevent the grid crafting matrix inventory listener from resetting the list.
            if (container instanceof GridContainerMenu containerMenu) {
                IGrid grid = containerMenu.getGrid();
                //exclude output slots
                if (grid instanceof GridNetworkNode networkNode && slot.getSlotIndex() < networkNode.getAllowedTagList().getAllowedItemTags().size()) {
                    Set<ResourceLocation> list = new HashSet<>(networkNode.getAllowedTagList().getAllowedItemTags().get(slot.getSlotIndex()));

                    postAction = () -> {
                        networkNode.getAllowedTagList().setAllowedItemTags(slot.getSlotIndex(), list);
                        networkNode.markDirty();
                    };
                }
            }

            slot.set(message.stack);
            postAction.run();
        }
    }

    private int getTieredStackInteractCount(ItemStack stack) {
        return (int) (stack.getMaxStackSize() * Math.pow(slotsMultiplier, 3));
    }
}