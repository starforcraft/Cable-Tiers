package com.ultramega.cabletiers.common.autocrafting.sidedinput;

import com.refinedmods.refinedstorage.api.core.CoreValidations;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;

import java.util.Optional;

import net.minecraft.core.Direction;

public record SidedResourceAmount(ResourceAmount resource, Optional<Direction> inputDirection) {
    /**
     * @param resource the resource, must be non-null
     */
    public SidedResourceAmount {
        validate(resource);
    }

    public static void validate(final ResourceAmount resource) {
        CoreValidations.validateNotNull(resource, "Resource must not be null");
    }
}
