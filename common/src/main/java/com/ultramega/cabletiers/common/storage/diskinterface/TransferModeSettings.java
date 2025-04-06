package com.ultramega.cabletiers.common.storage.diskinterface;

import com.refinedmods.refinedstorage.api.network.impl.node.storagetransfer.StorageTransferMode;

/**
 * Exact copy of {@link com.refinedmods.refinedstorage.common.storage.diskinterface.TransferModeSettings}
 */
class TransferModeSettings {
    private static final int INSERT_INTO_NETWORK = 0;
    private static final int EXTRACT_FROM_NETWORK = 1;

    private TransferModeSettings() {
    }

    static StorageTransferMode getTransferMode(final int transferMode) {
        if (transferMode == EXTRACT_FROM_NETWORK) {
            return StorageTransferMode.EXTRACT_FROM_NETWORK;
        }
        return StorageTransferMode.INSERT_INTO_NETWORK;
    }

    static int getTransferMode(final StorageTransferMode transferMode) {
        return switch (transferMode) {
            case INSERT_INTO_NETWORK -> INSERT_INTO_NETWORK;
            case EXTRACT_FROM_NETWORK -> EXTRACT_FROM_NETWORK;
        };
    }
}
