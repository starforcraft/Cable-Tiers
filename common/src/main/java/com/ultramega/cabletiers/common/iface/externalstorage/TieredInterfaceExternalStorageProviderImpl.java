package com.ultramega.cabletiers.common.iface.externalstorage;

import com.ultramega.cabletiers.common.iface.TieredInterfaceNetworkNode;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.impl.node.iface.InterfaceExportState;
import com.refinedmods.refinedstorage.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNodeActor;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TieredInterfaceExternalStorageProviderImpl implements TieredInterfaceExternalStorageProvider {
    private final TieredInterfaceNetworkNode networkNode;

    public TieredInterfaceExternalStorageProviderImpl(final TieredInterfaceNetworkNode networkNode) {
        this.networkNode = networkNode;
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (isAnotherInterfaceActingAsExternalStorage(actor)) {
            return 0;
        }
        final InterfaceExportState exportState = networkNode.getExportState();
        if (exportState == null) {
            return 0;
        }
        return exportState.extract(resource, amount, action);
    }

    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (isAnotherInterfaceActingAsExternalStorage(actor)) {
            return 0;
        }
        final InterfaceExportState exportState = networkNode.getExportState();
        if (exportState == null) {
            return 0;
        }
        return exportState.insert(resource, amount, action);
    }

    private boolean isAnotherInterfaceActingAsExternalStorage(final Actor actor) {
        return actor instanceof NetworkNodeActor(NetworkNode node)
            && node instanceof TieredInterfaceNetworkNode actingInterface
            && actingInterface.isActingAsExternalStorage();
    }

    @Override
    public Iterator<ResourceAmount> iterator() {
        final InterfaceExportState exportState = networkNode.getExportState();
        if (exportState == null) {
            return Collections.emptyIterator();
        }
        final List<ResourceAmount> slots = new ArrayList<>();
        for (int i = 0; i < exportState.getSlots(); ++i) {
            final ResourceKey resource = exportState.getExportedResource(i);
            if (resource == null) {
                continue;
            }
            slots.add(new ResourceAmount(resource, exportState.getExportedAmount(i)));
        }
        return slots.iterator();
    }

    @Override
    public TieredInterfaceNetworkNode getInterface() {
        return networkNode;
    }
}
