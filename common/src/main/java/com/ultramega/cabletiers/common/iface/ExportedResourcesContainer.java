package com.ultramega.cabletiers.common.iface;

import com.ultramega.cabletiers.common.CableTiers;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.core.CoreValidations;
import com.refinedmods.refinedstorage.api.network.impl.node.iface.InterfaceExportState;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.storage.root.FuzzyRootStorage;
import com.refinedmods.refinedstorage.common.api.support.resource.FuzzyModeNormalizer;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.FilterWithFuzzyMode;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;

public class ExportedResourcesContainer extends ResourceContainerImpl implements InterfaceExportState {
    private final FilterWithFuzzyMode filter;

    ExportedResourcesContainer(final CableTiers tier, final int size, final FilterWithFuzzyMode filter) {
        super(
            size,
            (resource) -> TieredInterfaceBlockEntity.getTransferQuota(tier, resource),
            RefinedStorageApi.INSTANCE.getItemResourceFactory(),
            RefinedStorageApi.INSTANCE.getAlternativeResourceFactories()
        );
        this.filter = filter;
    }

    @Override
    public int getSlots() {
        return size();
    }

    @Override
    public Collection<ResourceKey> expandExportCandidates(final RootStorage rootStorage,
                                                          final ResourceKey resource) {
        if (!filter.isFuzzyMode()) {
            return Collections.singletonList(resource);
        }
        if (!(rootStorage instanceof FuzzyRootStorage fuzzyRootStorage)) {
            return Collections.singletonList(resource);
        }
        return fuzzyRootStorage.getFuzzy(resource);
    }

    @Override
    public boolean isExportedResourceValid(final ResourceKey want, final ResourceKey got) {
        if (!filter.isFuzzyMode()) {
            return got.equals(want);
        }
        final ResourceKey normalizedGot = normalize(got);
        final ResourceKey normalizedWant = normalize(want);
        return normalizedGot.equals(normalizedWant);
    }

    private ResourceKey normalize(final ResourceKey resource) {
        if (resource instanceof FuzzyModeNormalizer normalizer) {
            return normalizer.normalize();
        }
        return resource;
    }

    @Nullable
    @Override
    public ResourceKey getRequestedResource(final int slotIndex) {
        return filter.getFilterContainer().getResource(slotIndex);
    }

    @Override
    public long getRequestedAmount(final int slotIndex) {
        return filter.getFilterContainer().getAmount(slotIndex);
    }

    @Nullable
    @Override
    public ResourceKey getExportedResource(final int slotIndex) {
        return getResource(slotIndex);
    }

    @Override
    public long getExportedAmount(final int slotIndex) {
        return getAmount(slotIndex);
    }

    @Override
    public void setExportSlot(final int slotIndex, final ResourceKey resource, final long amount) {
        set(slotIndex, new ResourceAmount(resource, amount));
    }

    @Override
    public void shrinkExportedAmount(final int slotIndex, final long amount) {
        shrink(slotIndex, amount);
    }

    @Override
    public void growExportedAmount(final int slotIndex, final long amount) {
        grow(slotIndex, amount);
    }

    /**
     * Copy of the super method except it's checking for the slot max stack size instead of the interface export limit
     */
    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action) {
        CoreValidations.validateNotNull(resource, "Resource to insert must not be null.");
        CoreValidations.validateLargerThanZero(amount, "Amount to insert must be larger than zero.");
        if (!(resource instanceof PlatformResourceKey platformResource)) {
            return 0L;
        }
        long remainder = amount;
        for (int i = 0; i < size(); ++i) {
            final ResourceAmount slot = get(i);
            if (slot == null) {
                remainder -= insertIntoEmptySlot(i, platformResource, action, remainder);
            } else if (slot.resource().equals(resource)) {
                remainder -= insertIntoExistingSlot(
                    i,
                    platformResource,
                    action,
                    remainder,
                    slot
                );
            }
            if (remainder == 0) {
                break;
            }
        }
        return amount - remainder;
    }

    private long insertIntoEmptySlot(final int slotIndex,
                                     final PlatformResourceKey resource,
                                     final Action action,
                                     final long amount) {
        final long inserted = Math.min(Math.max(getMaxAmount(resource), resource.getInterfaceExportLimit()), amount);
        if (action == Action.EXECUTE) {
            set(slotIndex, new ResourceAmount(resource, inserted));
        }
        return inserted;
    }

    private long insertIntoExistingSlot(final int slotIndex,
                                        final PlatformResourceKey resource,
                                        final Action action,
                                        final long amount,
                                        final ResourceAmount existing) {
        final long spaceRemaining = Math.max(getMaxAmount(resource), resource.getInterfaceExportLimit()) - existing.amount();
        final long inserted = Math.min(spaceRemaining, amount);
        if (action == Action.EXECUTE) {
            grow(slotIndex, inserted);
        }
        return inserted;
    }
}
