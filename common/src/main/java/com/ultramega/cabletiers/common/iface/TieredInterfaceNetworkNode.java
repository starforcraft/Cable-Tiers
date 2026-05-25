package com.ultramega.cabletiers.common.iface;

import com.ultramega.cabletiers.common.iface.externalstorage.TieredInterfaceExternalStorageProvider;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.api.network.impl.node.externalstorage.ExposedExternalStorage;
import com.refinedmods.refinedstorage.api.network.impl.node.iface.InterfaceExportState;
import com.refinedmods.refinedstorage.api.network.impl.node.iface.InterfaceNetworkNode.OnMissingResources;
import com.refinedmods.refinedstorage.api.network.impl.node.iface.InterfaceTransferResult;
import com.refinedmods.refinedstorage.api.network.node.NetworkNodeActor;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.Storage;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;

import java.util.Collection;
import java.util.function.ToLongFunction;

import org.jspecify.annotations.Nullable;

public class TieredInterfaceNetworkNode extends AbstractNetworkNode {
    private long energyUsage;
    private final Actor actor = new NetworkNodeActor(this);
    @Nullable
    private InterfaceExportState exportState;
    @Nullable
    private InterfaceTransferResult @Nullable[] lastResults;
    private ToLongFunction<ResourceKey> transferQuotaProvider = resource -> Long.MAX_VALUE;
    private OnMissingResources onMissingResources = OnMissingResources.EMPTY;

    public TieredInterfaceNetworkNode(final long energyUsage) {
        this.energyUsage = energyUsage;
    }

    public void setTransferQuotaProvider(final ToLongFunction<ResourceKey> transferQuotaProvider) {
        this.transferQuotaProvider = transferQuotaProvider;
    }

    public void setOnMissingResources(final OnMissingResources onMissingResources) {
        this.onMissingResources = onMissingResources;
    }

    public boolean isActingAsExternalStorage() {
        if (this.network == null) {
            return false;
        }
        return this.network.getComponent(StorageNetworkComponent.class).hasSource(
            this::isStorageAnExternalStorageProviderThatReferencesMe
        );
    }

    private boolean isStorageAnExternalStorageProviderThatReferencesMe(final Storage storage) {
        return storage instanceof ExposedExternalStorage proxy
            && proxy.getExternalStorageProvider() instanceof TieredInterfaceExternalStorageProvider interfaceProvider
            && interfaceProvider.getInterface() == this;
    }

    @Nullable
    public InterfaceExportState getExportState() {
        return this.exportState;
    }

    public void setEnergyUsage(final long energyUsage) {
        this.energyUsage = energyUsage;
    }

    public void setExportState(@Nullable final InterfaceExportState exportState) {
        this.exportState = exportState;
        this.lastResults = exportState != null
            ? new InterfaceTransferResult[exportState.getSlots()]
            : null;
    }

    @Override
    public void doWork() {
        super.doWork();
        if (this.exportState == null || this.network == null || !this.isActive()) {
            return;
        }
        final RootStorage storage = this.network.getComponent(StorageNetworkComponent.class);
        for (int i = 0; i < this.exportState.getSlots(); ++i) {
            this.updateSlot(this.exportState, i, storage);
        }
    }

    private void updateSlot(final InterfaceExportState state, final int index, final RootStorage storage) {
        final ResourceKey want = state.getRequestedResource(index);
        final ResourceKey got = state.getExportedResource(index);
        if (want == null && got == null) {
            this.updateResult(index, null);
        } else if (want == null) {
            this.clearSlot(state, index, got, storage);
        } else if (got == null) {
            this.updateEmptySlot(state, index, want, storage);
        } else {
            final boolean valid = state.isExportedResourceValid(want, got);
            if (!valid) {
                this.clearSlot(state, index, got, storage);
            } else {
                this.updateSlot(state, index, got, storage);
            }
        }
    }

    private void updateSlot(final InterfaceExportState state,
                            final int slot,
                            final ResourceKey got,
                            final RootStorage storage) {
        final long wantedAmount = state.getRequestedAmount(slot);
        final long currentAmount = state.getExportedAmount(slot);
        final long difference = wantedAmount - currentAmount;
        if (difference > 0) {
            this.extractMoreFromStorage(state, slot, got, difference, storage);
        } else if (difference < 0) {
            this.insertOverflowToStorage(state, slot, got, difference, storage);
        }
    }

    private void clearSlot(final InterfaceExportState state,
                           final int slot,
                           final ResourceKey got,
                           final RootStorage storage) {
        final long currentAmount = state.getExportedAmount(slot);
        final long inserted = storage.insert(
            got,
            Math.min(currentAmount, this.transferQuotaProvider.applyAsLong(got)),
            Action.EXECUTE,
            this.actor
        );
        if (inserted == 0) {
            this.updateResult(slot, InterfaceTransferResult.STORAGE_DOES_NOT_ACCEPT_RESOURCE);
            return;
        }
        state.shrinkExportedAmount(slot, inserted);
        final boolean empty = state.getExportedAmount(slot) == 0;
        final boolean stillWantSomething = state.getRequestedResource(slot) != null;
        this.updateResult(slot, empty && !stillWantSomething ? null : InterfaceTransferResult.EXPORTED);
    }

    private void updateEmptySlot(final InterfaceExportState state,
                                 final int slot,
                                 final ResourceKey resource,
                                 final RootStorage storage) {
        final long wantedAmount = state.getRequestedAmount(slot);
        final long correctedAmount = Math.min(this.transferQuotaProvider.applyAsLong(resource), wantedAmount);
        final Collection<ResourceKey> candidates = state.expandExportCandidates(storage, resource);
        for (final ResourceKey candidate : candidates) {
            final long extracted = storage.extract(candidate, correctedAmount, Action.EXECUTE, this.actor);
            if (extracted > 0) {
                state.setExportSlot(slot, candidate, extracted);
                this.updateResult(slot, InterfaceTransferResult.EXPORTED);
                return;
            }
        }
        if (this.network == null) {
            return;
        }
        final InterfaceTransferResult result = this.onMissingResources.onMissingResources(resource, correctedAmount, this.actor,
            this.network);
        this.updateResult(slot, result);
    }

    private void extractMoreFromStorage(final InterfaceExportState state,
                                        final int slot,
                                        final ResourceKey resource,
                                        final long amount,
                                        final RootStorage storage) {
        final long correctedAmount = Math.min(this.transferQuotaProvider.applyAsLong(resource), amount);
        final long extracted = storage.extract(resource, correctedAmount, Action.EXECUTE, this.actor);
        if (extracted == 0) {
            if (this.network == null) {
                return;
            }
            final InterfaceTransferResult result =
                this.onMissingResources.onMissingResources(resource, correctedAmount, this.actor, this.network);
            this.updateResult(slot, result);
            return;
        }
        state.growExportedAmount(slot, extracted);
        this.updateResult(slot, InterfaceTransferResult.EXPORTED);
    }

    private void insertOverflowToStorage(final InterfaceExportState state,
                                         final int slot,
                                         final ResourceKey resource,
                                         final long amount,
                                         final RootStorage storage) {
        final long correctedAmount = Math.min(this.transferQuotaProvider.applyAsLong(resource), Math.abs(amount));
        final long inserted = storage.insert(resource, correctedAmount, Action.EXECUTE, this.actor);
        if (inserted == 0) {
            this.updateResult(slot, InterfaceTransferResult.STORAGE_DOES_NOT_ACCEPT_RESOURCE);
            return;
        }
        state.shrinkExportedAmount(slot, inserted);
        this.updateResult(slot, InterfaceTransferResult.EXPORTED);
    }

    private void updateResult(final int slot, @Nullable final InterfaceTransferResult result) {
        if (this.lastResults == null) {
            return;
        }
        this.lastResults[slot] = result;
    }

    @Nullable
    public InterfaceTransferResult getLastResult(final int slot) {
        if (this.lastResults == null) {
            return null;
        }
        return this.lastResults[slot];
    }

    @Override
    public long getEnergyUsage() {
        return this.energyUsage;
    }
}
