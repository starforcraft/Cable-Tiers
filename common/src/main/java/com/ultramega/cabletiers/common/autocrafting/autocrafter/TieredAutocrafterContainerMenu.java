package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.packet.c2s.TieredAutocrafterNameChangePacket;
import com.ultramega.cabletiers.common.packet.s2c.TieredAutocrafterLockedUpdatePacket;
import com.ultramega.cabletiers.common.packet.s2c.TieredAutocrafterNameUpdatePacket;
import com.ultramega.cabletiers.common.registry.Menus;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.autocrafting.PatternInventory;
import com.refinedmods.refinedstorage.common.autocrafting.PatternSlot;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterContainerMenu;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterData;
import com.refinedmods.refinedstorage.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage.common.support.FilteredContainer;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeSlot;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.RateLimiter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TieredAutocrafterContainerMenu extends AbstractBaseContainerMenu {
    private static final int PATTERN_SLOT_X = 8;
    private static final int PATTERN_SLOT_Y = 20;

    private final CableTiers tier;
    private final Player player;
    private final boolean partOfChain;
    private final boolean headOfChain;
    private boolean locked;
    private final RateLimiter nameRateLimiter = RateLimiter.create(0.5);

    @Nullable
    private TieredAutocrafterBlockEntity autocrafter;
    @Nullable
    private AutocrafterContainerMenu.Listener listener;
    private Component name;

    public TieredAutocrafterContainerMenu(final int syncId,
                                          final Inventory playerInventory,
                                          final AutocrafterData data,
                                          final CableTiers tier) {
        super(Menus.INSTANCE.getTieredAutocrafters(tier), syncId);
        this.tier = tier;
        this.player = playerInventory.player;
        registerProperty(new ClientProperty<>(AutocrafterPropertyTypes.LOCK_MODE, LockMode.NEVER));
        registerProperty(new ClientProperty<>(AutocrafterPropertyTypes.PRIORITY, 0));
        registerProperty(new ClientProperty<>(AutocrafterPropertyTypes.VISIBLE_TO_THE_AUTOCRAFTER_MANAGER, true));
        registerProperty(new ClientProperty<>(AutocrafterPropertyTypes.ACT_AS_IMPORTER, false));
        addSlots(
            new PatternInventory(tier.getFilterSlotsCount(), playerInventory.player::level),
            new UpgradeContainer(UpgradeDestinations.AUTOCRAFTER)
        );
        this.name = Component.empty();
        this.partOfChain = data.partOfChain();
        this.headOfChain = data.headOfChain();
        this.locked = data.locked();
    }

    public TieredAutocrafterContainerMenu(final int syncId,
                                          final Inventory playerInventory,
                                          final TieredAutocrafterBlockEntity autocrafter,
                                          final CableTiers tier) {
        super(Menus.INSTANCE.getTieredAutocrafters(tier), syncId);
        this.tier = tier;
        this.autocrafter = autocrafter;
        this.player = playerInventory.player;
        this.name = autocrafter.getDisplayName();
        this.partOfChain = false;
        this.headOfChain = false;
        this.locked = autocrafter.isLocked();
        registerProperty(new ServerProperty<>(
            AutocrafterPropertyTypes.LOCK_MODE,
            autocrafter::getLockMode,
            autocrafter::setLockMode
        ));
        registerProperty(new ServerProperty<>(
            AutocrafterPropertyTypes.PRIORITY,
            autocrafter::getPriority,
            autocrafter::setPriority
        ));
        registerProperty(new ServerProperty<>(
            AutocrafterPropertyTypes.VISIBLE_TO_THE_AUTOCRAFTER_MANAGER,
            autocrafter::isVisibleToTheAutocrafterManager,
            autocrafter::setVisibleToTheAutocrafterManager
        ));
        registerProperty(new ServerProperty<>(
            AutocrafterPropertyTypes.ACT_AS_IMPORTER,
            autocrafter::isActAsImporter,
            autocrafter::setActAsImporter
        ));
        addSlots(autocrafter.getPatternContainer(), autocrafter.getUpgradeContainer());
    }

    boolean canChangeName() {
        return !partOfChain;
    }

    boolean isPartOfChain() {
        return partOfChain;
    }

    boolean isHeadOfChain() {
        return headOfChain;
    }

    boolean isLocked() {
        return locked;
    }

    void setListener(@Nullable final AutocrafterContainerMenu.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (autocrafter == null) {
            return;
        }
        if (nameRateLimiter.tryAcquire()) {
            detectNameChange();
        }
        final boolean newLocked = autocrafter.isLocked();
        if (locked != newLocked) {
            locked = newLocked;
            Platform.INSTANCE.sendPacketToClient((ServerPlayer) player, new TieredAutocrafterLockedUpdatePacket(locked));
        }
    }

    @Override
    public boolean stillValid(final Player p) {
        if (autocrafter == null) {
            return true;
        }
        return Container.stillValidBlockEntity(autocrafter, p);
    }

    private void detectNameChange() {
        if (autocrafter == null) {
            return;
        }
        final Component newName = autocrafter.getDisplayName();
        if (!newName.equals(name)) {
            this.name = newName;
            Platform.INSTANCE.sendPacketToClient((ServerPlayer) player, new TieredAutocrafterNameUpdatePacket(newName));
        }
    }

    private void addSlots(final FilteredContainer patternContainer, final UpgradeContainer upgradeContainer) {
        for (int i = 0; i < patternContainer.getContainerSize(); ++i) {
            addSlot(createPatternSlot(patternContainer, i, player.level()));
        }
        if (tier != CableTiers.CREATIVE) {
            for (int i = 0; i < upgradeContainer.getContainerSize(); ++i) {
                addSlot(new UpgradeSlot(upgradeContainer, i, 187, 6 + (i * 18)));
            }
        }
        addPlayerInventory(player.getInventory(), 8, tier.getPlayerInventoryY());
        transferManager.addBiTransfer(player.getInventory(), upgradeContainer);
        transferManager.addBiTransfer(player.getInventory(), patternContainer);
    }

    private Slot createPatternSlot(final FilteredContainer patternContainer,
                                   final int i,
                                   final Level level) {
        final int x = PATTERN_SLOT_X + (18 * (i % 9));
        return new PatternSlot(patternContainer, i, x, PATTERN_SLOT_Y + 18 * (i / 9), level);
    }

    public boolean containsPattern(final ItemStack stack) {
        for (final Slot slot : slots) {
            if (slot instanceof PatternSlot patternSlot && patternSlot.getItem() == stack) {
                return true;
            }
        }
        return false;
    }

    public void changeName(final String newName) {
        if (partOfChain) {
            return;
        }
        if (autocrafter != null) {
            autocrafter.setCustomName(newName);
            detectNameChange();
        } else {
            Platform.INSTANCE.sendPacketToServer(new TieredAutocrafterNameChangePacket(newName));
        }
    }

    public void nameChanged(final Component newName) {
        if (listener != null) {
            listener.nameChanged(newName);
        }
    }

    public void lockedChanged(final boolean newLocked) {
        this.locked = newLocked;
        if (listener != null) {
            listener.lockedChanged(newLocked);
        }
    }
}
