package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.packet.c2s.TieredAutocrafterNameChangePacket;
import com.ultramega.cabletiers.common.packet.s2c.TieredAutocrafterLockedUpdatePacket;
import com.ultramega.cabletiers.common.packet.s2c.TieredAutocrafterNameUpdatePacket;
import com.ultramega.cabletiers.common.registry.Menus;
import com.ultramega.cabletiers.common.utils.AdvancedPatternSlot;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.autocrafting.PatternInventory;
import com.refinedmods.refinedstorage.common.autocrafting.PatternSlot;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterContainerMenu;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterData;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.LockMode;
import com.refinedmods.refinedstorage.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage.common.support.FilteredContainer;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeSlot;

import java.util.ArrayList;
import java.util.List;

import com.google.common.util.concurrent.RateLimiter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class TieredAutocrafterContainerMenu extends AbstractBaseContainerMenu {
    protected static final int PATTERN_VISIBLE_ROWS = 6;
    private static final int PATTERN_COLUMNS = 9;

    private static final int PATTERN_SLOT_X = 8;
    private static final int PATTERN_SLOT_Y = 20;

    private final CableTiers tier;
    private final Player player;
    private final boolean partOfChain;
    private final boolean headOfChain;
    private boolean locked;
    private final RateLimiter nameRateLimiter = RateLimiter.create(0.5);

    private final List<PatternSlot> patternSlots = new ArrayList<>();
    private int patternScrollOffset;

    @Nullable
    private TieredAutocrafterBlockEntity autocrafter;
    private AutocrafterContainerMenu.@Nullable Listener listener;
    private Component name;

    public TieredAutocrafterContainerMenu(final int syncId,
                                          final Inventory playerInventory,
                                          final AutocrafterData data,
                                          final CableTiers tier) {
        super(Menus.INSTANCE.getTieredAutocrafters(tier), syncId);
        this.tier = tier;
        this.player = playerInventory.player;
        this.registerProperty(new ClientProperty<>(AutocrafterPropertyTypes.LOCK_MODE, LockMode.NEVER));
        this.registerProperty(new ClientProperty<>(AutocrafterPropertyTypes.PRIORITY, 0));
        this.registerProperty(new ClientProperty<>(AutocrafterPropertyTypes.VISIBLE_TO_THE_AUTOCRAFTER_MANAGER, true));
        this.registerProperty(new ClientProperty<>(AutocrafterPropertyTypes.IMPORT_MODE, ImportMode.DONT_IMPORT));
        this.addSlots(
            new PatternInventory(tier.getAutocrafterPatternSlotCount(), playerInventory.player::level),
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
        this.registerProperty(new ServerProperty<>(
            AutocrafterPropertyTypes.LOCK_MODE,
            autocrafter::getLockMode,
            autocrafter::setLockMode
        ));
        this.registerProperty(new ServerProperty<>(
            AutocrafterPropertyTypes.PRIORITY,
            autocrafter::getPriority,
            autocrafter::setPriority
        ));
        this.registerProperty(new ServerProperty<>(
            AutocrafterPropertyTypes.VISIBLE_TO_THE_AUTOCRAFTER_MANAGER,
            autocrafter::isVisibleToTheAutocrafterManager,
            autocrafter::setVisibleToTheAutocrafterManager
        ));
        this.registerProperty(new ServerProperty<>(
            AutocrafterPropertyTypes.IMPORT_MODE,
            autocrafter::getImportMode,
            autocrafter::setImportMode
        ));
        this.addSlots(autocrafter.getPatternContainer(), autocrafter.getUpgradeContainer());
    }

    boolean canChangeName() {
        return !this.partOfChain;
    }

    boolean isPartOfChain() {
        return this.partOfChain;
    }

    boolean isHeadOfChain() {
        return this.headOfChain;
    }

    boolean isLocked() {
        return this.locked;
    }

    void setListener(final AutocrafterContainerMenu.@Nullable Listener listener) {
        this.listener = listener;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (this.autocrafter == null) {
            return;
        }
        if (this.nameRateLimiter.tryAcquire()) {
            this.detectNameChange();
        }
        final boolean newLocked = this.autocrafter.isLocked();
        if (this.locked != newLocked) {
            this.locked = newLocked;
            Platform.INSTANCE.sendPacketToClient((ServerPlayer) this.player, new TieredAutocrafterLockedUpdatePacket(this.locked));
        }
    }

    @Override
    public boolean stillValid(final Player p) {
        if (this.autocrafter == null) {
            return true;
        }
        return Container.stillValidBlockEntity(this.autocrafter, p);
    }

    private void detectNameChange() {
        if (this.autocrafter == null) {
            return;
        }
        final Component newName = this.autocrafter.getDisplayName();
        if (!newName.equals(this.name)) {
            this.name = newName;
            Platform.INSTANCE.sendPacketToClient((ServerPlayer) this.player, new TieredAutocrafterNameUpdatePacket(newName));
        }
    }

    private void addSlots(final FilteredContainer patternContainer, final UpgradeContainer upgradeContainer) {
        this.patternSlots.clear();
        for (int i = 0; i < patternContainer.getContainerSize(); ++i) {
            final PatternSlot slot = this.createPatternSlot(patternContainer, i, this.player.level());
            this.patternSlots.add(slot);
            this.addSlot(slot);
        }

        this.updatePatternSlotsForScroll();

        if (this.tier != CableTiers.CREATIVE) {
            for (int i = 0; i < upgradeContainer.getContainerSize(); ++i) {
                this.addSlot(new UpgradeSlot(upgradeContainer, i, this.tier == CableTiers.ELITE ? 187 : 204, 6 + (i * SLOT_SIZE)));
            }
        }

        this.addPlayerInventory(this.player.getInventory(), 8, this.tier == CableTiers.ULTRA ? 145 : this.tier.getPlayerInventoryY());

        this.transferManager.addBiTransfer(this.player.getInventory(), upgradeContainer);
        this.transferManager.addBiTransfer(this.player.getInventory(), patternContainer);
    }

    private PatternSlot createPatternSlot(final FilteredContainer patternContainer,
                                          final int i,
                                          final Level level) {
        final int x = PATTERN_SLOT_X + (SLOT_SIZE * (i % PATTERN_COLUMNS));
        final int y = PATTERN_SLOT_Y + (SLOT_SIZE * (i / PATTERN_COLUMNS));
        return new AdvancedPatternSlot(patternContainer, i, x, y, level, this.tier == CableTiers.ELITE);
    }

    public void setPatternScrollOffset(final int offset) {
        final int rowsExcludingVisibleOnes = this.getPatternTotalRows() - PATTERN_VISIBLE_ROWS;
        final int clampedOffset = Math.clamp(offset, 0, Math.max(0, rowsExcludingVisibleOnes * SLOT_SIZE));
        if (this.patternScrollOffset == clampedOffset) {
            return;
        }

        this.patternScrollOffset = clampedOffset;
        this.updatePatternSlotsForScroll();
    }

    private void updatePatternSlotsForScroll() {
        for (int row = 0; row < Math.max(this.getPatternTotalRows(), PATTERN_VISIBLE_ROWS); ++row) {
            final int rowY = PATTERN_SLOT_Y + (row * SLOT_SIZE) - this.patternScrollOffset;
            final boolean isOutOfFrame = rowY < PATTERN_SLOT_Y - SLOT_SIZE || rowY > PATTERN_SLOT_Y + getPatternViewportHeight() - 2;

            for (int column = 0; column < PATTERN_COLUMNS; ++column) {
                final int index = (row * PATTERN_COLUMNS) + column;
                if (index >= this.patternSlots.size()) {
                    continue;
                }

                final PatternSlot slot = this.patternSlots.get(index);
                Platform.INSTANCE.setSlotY(slot, isOutOfFrame ? -10_000 : rowY);
            }
        }
    }

    public List<PatternSlot> getPatternSlots() {
        return this.patternSlots;
    }

    protected int getPatternTotalRows() {
        return (this.patternSlots.size() + PATTERN_COLUMNS - 1) / PATTERN_COLUMNS;
    }

    protected static int getPatternViewportHeight() {
        return PATTERN_VISIBLE_ROWS * SLOT_SIZE;
    }

    public boolean containsPattern(final ItemStack stack) {
        for (final Slot slot : this.slots) {
            if (slot instanceof PatternSlot patternSlot && patternSlot.getItem() == stack) {
                return true;
            }
        }
        return false;
    }

    public void changeName(final String newName) {
        if (this.partOfChain) {
            return;
        }
        if (this.autocrafter != null) {
            this.autocrafter.setCustomName(newName);
            this.detectNameChange();
        } else {
            Platform.INSTANCE.sendPacketToServer(new TieredAutocrafterNameChangePacket(newName));
        }
    }

    public void nameChanged(final Component newName) {
        if (this.listener != null) {
            this.listener.nameChanged(newName);
        }
    }

    public void lockedChanged(final boolean newLocked) {
        this.locked = newLocked;
        if (this.listener != null) {
            this.listener.lockedChanged(newLocked);
        }
    }
}
