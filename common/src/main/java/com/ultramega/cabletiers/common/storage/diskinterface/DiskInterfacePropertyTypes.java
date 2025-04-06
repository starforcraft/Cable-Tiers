package com.ultramega.cabletiers.common.storage.diskinterface;

import com.refinedmods.refinedstorage.api.network.impl.node.storagetransfer.StorageTransferMode;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyType;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

/**
 * Exact copy of {@link com.refinedmods.refinedstorage.common.storage.diskinterface.DiskInterfacePropertyTypes}
 */
class DiskInterfacePropertyTypes {
    public static final PropertyType<StorageTransferMode> TRANSFER_MODE = new PropertyType<>(
        createIdentifier("transfer_mode"),
        TransferModeSettings::getTransferMode,
        TransferModeSettings::getTransferMode
    );

    private DiskInterfacePropertyTypes() {
    }
}
