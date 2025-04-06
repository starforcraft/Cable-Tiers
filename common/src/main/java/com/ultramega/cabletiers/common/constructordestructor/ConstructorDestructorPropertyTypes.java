package com.ultramega.cabletiers.common.constructordestructor;

import com.refinedmods.refinedstorage.common.support.containermenu.PropertyType;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

/**
 * Exact copy of {@link com.refinedmods.refinedstorage.common.constructordestructor.ConstructorDestructorPropertyTypes}
 */
final class ConstructorDestructorPropertyTypes {
    static final PropertyType<Boolean> PICKUP_ITEMS = PropertyTypes.createBooleanProperty(
        createIdentifier("pickup_items")
    );
    static final PropertyType<Boolean> DROP_ITEMS = PropertyTypes.createBooleanProperty(
        createIdentifier("drop_items")
    );

    private ConstructorDestructorPropertyTypes() {
    }
}
