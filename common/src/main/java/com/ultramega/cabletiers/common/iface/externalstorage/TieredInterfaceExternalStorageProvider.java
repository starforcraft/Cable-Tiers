package com.ultramega.cabletiers.common.iface.externalstorage;

import com.ultramega.cabletiers.common.iface.TieredInterfaceNetworkNode;

import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;

import javax.annotation.Nullable;

public interface TieredInterfaceExternalStorageProvider extends ExternalStorageProvider {
    @Nullable
    TieredInterfaceNetworkNode getInterface();
}
