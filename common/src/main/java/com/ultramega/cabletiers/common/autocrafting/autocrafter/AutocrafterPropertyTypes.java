package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.LockMode;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyType;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

/**
 * Almost exact copy of {@link com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterPropertyTypes}
 */
final class AutocrafterPropertyTypes {
    static final PropertyType<LockMode> LOCK_MODE = new PropertyType<>(
        createIdentifier("lock_mode"),
        LockModeSettings::getLockMode,
        LockModeSettings::getLockMode
    );

    static final PropertyType<Integer> PRIORITY = PropertyTypes.createIntegerProperty(
        createIdentifier("crafter_priority")
    );

    static final PropertyType<Boolean> VISIBLE_TO_THE_AUTOCRAFTER_MANAGER = PropertyTypes.createBooleanProperty(
        createIdentifier("visible_to_the_autocrafter_manager")
    );

    static final PropertyType<ImportMode> IMPORT_MODE = new PropertyType<>(
        createIdentifier("import_mode"),
        ImportModeSettings::getImportMode,
        ImportModeSettings::getImportMode
    );

    private AutocrafterPropertyTypes() {
    }
}
